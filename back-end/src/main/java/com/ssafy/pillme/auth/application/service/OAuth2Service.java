package com.ssafy.pillme.auth.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.pillme.auth.application.exception.oauth.InvalidOAuthStateException;
import com.ssafy.pillme.auth.application.exception.security.InvalidMemberInfoException;
import com.ssafy.pillme.auth.application.exception.security.UnverifiedPhoneNumberException;
import com.ssafy.pillme.auth.application.exception.server.JsonParsingException;
import com.ssafy.pillme.auth.application.exception.validation.DuplicateEmailAddressException;
import com.ssafy.pillme.auth.application.exception.validation.DuplicateMemberNicknameException;
import com.ssafy.pillme.auth.application.exception.validation.DuplicatePhoneNumberException;
import com.ssafy.pillme.auth.application.response.MemberResponse;
import com.ssafy.pillme.auth.application.response.TokenResponse;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.domain.vo.GoogleOAuthInfo;
import com.ssafy.pillme.auth.domain.vo.Provider;
import com.ssafy.pillme.auth.infrastructure.repository.MemberRepository;
import com.ssafy.pillme.auth.application.response.OAuth2Response;
import com.ssafy.pillme.auth.presentation.request.OAuthAdditionalInfoRequest;
import com.ssafy.pillme.auth.presentation.request.OAuthSignUpRequest;
import com.ssafy.pillme.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final AuthService authService;
    private final SmsService smsService;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    @Value("${FRONTEND_OAUTH_ADDITIONAL_PROFILE_URL}")
    private String frontendUrl;

    /**
     * 구글 로그인 처리
     */
    public OAuth2Response googleLogin(String code) {
        String accessToken = extractGoogleAccessToken(code);
        GoogleOAuthInfo memberInfo = extractGoogleOAuthInfo(accessToken);

        // 기존 회원 조회
        Member member = memberRepository.findByEmailAndProviderAndDeletedFalse(memberInfo.email(), Provider.GOOGLE)
                .orElse(null);

        // 신규 회원인 경우 회원정보 입력 페이지로 리다이렉트
        if (member == null) {
            // 임시 인증 정보 저장
            tokenService.saveTempAuthInfo(memberInfo.email(), Provider.GOOGLE.name(), 1800000); // 30분
            return createRedirectResponse(memberInfo.email(), memberInfo.name(), Provider.GOOGLE);
        }

        // 기존 회원인 경우 토큰 발급
        return createOAuth2TokenResponse(member);
    }

    /**
     * 리다이렉션 응답 생성
     */
    private OAuth2Response createRedirectResponse(String email, String name, Provider provider) {
        try {
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString());
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            String encodedProvider = URLEncoder.encode(provider.toString(), StandardCharsets.UTF_8.toString());

            String redirectUrl = String.format("%s?email=%s&name=%s&provider=%s",
                    frontendUrl, encodedEmail, encodedName, encodedProvider);
            return OAuth2Response.redirect(email, name, redirectUrl);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL 인코딩 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * OAuth2 회원가입
     */
    @Transactional
    public TokenResponse oauthSignUp(OAuthSignUpRequest request, String providerString) {
        Provider provider = Provider.valueOf(providerString.toUpperCase());

        // 이전 인증 정보 확인
        String savedProvider = tokenService.extractTempAuthInfo(request.email());
        if (savedProvider == null || !savedProvider.equals(provider.name())) {
            throw new InvalidOAuthStateException();
        }

        // 이메일 중복 체크 추가
        if (memberRepository.existsByEmailAndDeletedFalse(request.email())) {
            throw new DuplicateEmailAddressException();
        }

        // 닉네임 중복 확인
        if (memberRepository.existsByNicknameAndDeletedFalse(request.nickname())) {
            throw new DuplicateMemberNicknameException();
        }

        // 휴대전화 중복 확인
        if (memberRepository.existsByPhoneAndDeletedFalse(request.phone())) {
            throw new DuplicatePhoneNumberException();
        }

        // 휴대전화 인증 확인
//        if (!smsService.isVerified(request.phone())) {
//            throw new UnverifiedPhoneNumberException();
//        }

        // 회원 생성
        Member member = Member.builder()
                .email(request.email())
                .password(UUID.randomUUID().toString())
                .name(request.name())
                .nickname(request.nickname())
                .phone(request.phone())
                .birthday(request.birthday())
                .gender(request.gender())
                .oauth(true)
                .provider(provider)
                .build();

        Member savedMember = memberRepository.save(member);
        return createTokenResponse(savedMember);
    }

    private TokenResponse createTokenResponse(Member member) {
        String accessToken = jwtUtil.createAccessToken(member.getId(), member.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(member.getId(), member.getRole().name());

        tokenService.saveRefreshToken(member.getId(), refreshToken,
                jwtUtil.extractRefreshTokenValidityPeriod());

        return TokenResponse.of(
                accessToken,
                refreshToken,
                jwtUtil.extractAccessTokenValidityPeriod(),
                jwtUtil.extractRefreshTokenValidityPeriod()
        );
    }

    /**
     * OAuth2 토큰 응답 생성
     */
    private OAuth2Response createOAuth2TokenResponse(Member member) {
        String accessToken = jwtUtil.createAccessToken(member.getId(), member.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(member.getId(), member.getRole().name());

        tokenService.saveRefreshToken(member.getId(), refreshToken,
                jwtUtil.extractRefreshTokenValidityPeriod());

        TokenResponse tokenResponse = TokenResponse.of(
                accessToken,
                refreshToken,
                jwtUtil.extractAccessTokenValidityPeriod(),
                jwtUtil.extractRefreshTokenValidityPeriod()
        );

        return OAuth2Response.token(tokenResponse);
    }

    /**
     * 구글 액세스 토큰 추출
     */
    private String extractGoogleAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                String.class
        );

        try {
            return objectMapper.readTree(response.getBody()).get("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new JsonParsingException();
        }
    }

    /**
     * 구글 사용자 정보 추출
     */
    private GoogleOAuthInfo extractGoogleOAuthInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                request,
                String.class
        );

        try {
            var jsonNode = objectMapper.readTree(response.getBody());
            return new GoogleOAuthInfo(
                    jsonNode.get("email").asText(),
                    jsonNode.get("name").asText()
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException();
        }
    }
}