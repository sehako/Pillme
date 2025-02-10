package com.ssafy.pillme.auth.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.pillme.auth.application.exception.server.JsonParsingException;
import com.ssafy.pillme.auth.application.response.TokenResponse;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.domain.vo.GoogleOAuthInfo;
import com.ssafy.pillme.auth.domain.vo.NaverOAuthInfo;
import com.ssafy.pillme.auth.domain.vo.Provider;
import com.ssafy.pillme.auth.infrastructure.repository.MemberRepository;
import com.ssafy.pillme.auth.application.response.OAuth2Response;
import com.ssafy.pillme.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final AuthService authService;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    @Value("${FRONTEND_URL}/oauth/additional-info")
    private String frontendProfileUrl;

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
        String redirectUrl = String.format("%s?email=%s&name=%s&provider=%s",
                frontendProfileUrl, email, name, provider);
        return OAuth2Response.redirect(redirectUrl);
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
                    jsonNode.get("name").asText(),
                    jsonNode.get("given_name").asText() // 구글 오어스에서 nickname이 없어 이름만 가져와서 닉네임으로 사용하도록
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException();
        }
    }
}