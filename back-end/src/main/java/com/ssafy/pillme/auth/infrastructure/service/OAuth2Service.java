package com.ssafy.pillme.auth.infrastructure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.pillme.auth.application.response.TokenResponse;
import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.entity.User;
import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.auth.domain.vo.*;

import com.ssafy.pillme.auth.infrastructure.repository.UserRepository;
import com.ssafy.pillme.auth.presentation.response.OAuth2Response;
import com.ssafy.pillme.auth.util.JwtUtil;
import com.ssafy.pillme.global.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
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

    @Value("${NAVER_CLIENT_ID}")
    private String naverClientId;

    @Value("${NAVER_CLIENT_SECRET}")
    private String naverClientSecret;

    @Value("${NAVER_REDIRECT_URI}")
    private String naverRedirectUri;

    @Value("${FRONTEND_URL}")
    private String frontendProfileUrl;

    /**
     * 구글 로그인 처리
     */
    public OAuth2Response googleLogin(String code) {
        String accessToken = extractGoogleAccessToken(code);
        GoogleUserInfo userInfo = extractGoogleUserInfo(accessToken);

        // 기존 회원 조회
        User user = userRepository.findByEmailAndProvider(userInfo.email(), Provider.GOOGLE)
                .orElse(null);

        // 신규 회원인 경우 회원정보 입력 페이지로 리다이렉트
        if (user == null) {
            // 임시 인증 정보 저장
            tokenService.saveTempAuthInfo(userInfo.email(), Provider.GOOGLE.name(), 1800000); // 30분
            return createRedirectResponse(userInfo.email(), userInfo.name(), Provider.GOOGLE);
        }

        // 기존 회원인 경우 토큰 발급
        return createOAuth2TokenResponse(user);
    }

    /**
     * 네이버 로그인 처리
     */
    public OAuth2Response naverLogin(String code) {
        String accessToken = extractNaverAccessToken(code);
        NaverUserInfo userInfo = extractNaverUserInfo(accessToken);

        // 기존 회원 조회
        User user = userRepository.findByEmailAndProvider(userInfo.email(), Provider.NAVER)
                .orElse(null);

        // 신규 회원인 경우 회원정보 입력 페이지로 리다이렉트
        if (user == null) {
            // 임시 인증 정보 저장
            tokenService.saveTempAuthInfo(userInfo.email(), Provider.NAVER.name(), 1800000); // 30분
            return createRedirectResponse(userInfo.email(), userInfo.name(), Provider.NAVER);
        }

        // 기존 회원인 경우 토큰 발급
        return createOAuth2TokenResponse(user);
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
    private OAuth2Response createOAuth2TokenResponse(User user) {
        AuthenticationInfo authInfo = user.extractAuthenticationInfo();

        String accessToken = jwtUtil.createAccessToken(authInfo.identifier(), authInfo.authority().name());
        String refreshToken = jwtUtil.createRefreshToken(authInfo.identifier(), authInfo.authority().name());

        tokenService.saveRefreshToken(authInfo.identifier(), refreshToken,
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
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }
    }

    /**
     * 구글 사용자 정보 추출
     */
    private GoogleUserInfo extractGoogleUserInfo(String accessToken) {
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
            return new GoogleUserInfo(
                    jsonNode.get("email").asText(),
                    jsonNode.get("name").asText(),
                    jsonNode.get("given_name").asText() // 구글 오어스에서 nickname이 없어 이름만 가져와서 닉네임으로 사용하도록
            );
        } catch (JsonProcessingException e) {
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }
    }

    /**
     * 네이버 액세스 토큰 추출
     */
    private String extractNaverAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", code);
        params.add("state", "STATE_STRING");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://nid.naver.com/oauth2.0/token",
                request,
                String.class
        );

        try {
            return objectMapper.readTree(response.getBody()).get("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }
    }

    /**
     * 네이버 사용자 정보 추출
     */
    private NaverUserInfo extractNaverUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                request,
                String.class
        );

        try {
            var responseData = objectMapper.readTree(response.getBody()).get("response");
            return NaverUserInfo.of(
                    responseData.get("email").asText(),
                    responseData.get("name").asText(),
                    responseData.get("nickname").asText(),
                    responseData.get("gender").asText(),
                    responseData.get("birthyear").asText(),
                    responseData.get("birthday").asText(),
                    responseData.get("mobile").asText()
            );
        } catch (JsonProcessingException e) {
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }
    }
}