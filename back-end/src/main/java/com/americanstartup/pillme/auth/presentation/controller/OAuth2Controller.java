package com.americanstartup.pillme.auth.presentation.controller;

import com.americanstartup.pillme.auth.application.response.OAuth2Response;
import com.americanstartup.pillme.auth.application.response.TokenResponse;
import com.americanstartup.pillme.auth.application.service.AuthService;
import com.americanstartup.pillme.auth.application.service.OAuth2Service;
import com.americanstartup.pillme.auth.presentation.request.OAuthSignUpRequest;
import com.americanstartup.pillme.global.response.JSONResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {
    private final AuthService authService;
    private final OAuth2Service oauth2Service;

    /**
     * 구글 OAuth2 로그인
     */
    @GetMapping("/google")
    public ResponseEntity<?> googleCallback(@RequestParam String code, @RequestParam String state) {
        OAuth2Response response = oauth2Service.googleLogin(code);

        if (response.type() == OAuth2Response.ResponseType.REDIRECT) {
            // 리다이렉션이 필요한 경우 (신규 회원)
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(response.redirectUrl()));
            return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
        } else {
            // state 파라미터를 디코딩하여 원래 URL 얻기
            String originUrl = URLDecoder.decode(state, StandardCharsets.UTF_8);

            // 토큰 정보를 쿼리 파라미터로 포함하여 리다이렉션
            String redirectUrl = String.format("%s/login/success?accessToken=%s&refreshToken=%s",
                    originUrl,
                    response.tokenResponse().accessToken(),
                    response.tokenResponse().refreshToken()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));
            return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
        }
    }

    /**
     * OAuth2 회원가입 완료
     */
    @PostMapping("/signup")
    public ResponseEntity<JSONResponse<TokenResponse>> oauthSignUp(
            @Valid @RequestBody OAuthSignUpRequest request,
            @RequestParam String provider) {
        TokenResponse response = oauth2Service.oauthSignUp(request, provider);
        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    }
}