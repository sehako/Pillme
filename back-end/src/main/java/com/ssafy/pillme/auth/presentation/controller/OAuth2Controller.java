package com.ssafy.pillme.auth.presentation.controller;

import com.ssafy.pillme.auth.application.response.MemberResponse;
import com.ssafy.pillme.auth.application.response.OAuth2Response;
import com.ssafy.pillme.auth.application.response.TokenResponse;
import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.application.service.OAuth2Service;
import com.ssafy.pillme.auth.presentation.request.OAuthAdditionalInfoRequest;
import com.ssafy.pillme.auth.presentation.request.OAuthSignUpRequest;
import com.ssafy.pillme.global.response.JSONResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public ResponseEntity<?> googleCallback(@RequestParam String code) {
        OAuth2Response response = oauth2Service.googleLogin(code);

        if (response.type() == OAuth2Response.ResponseType.REDIRECT) {
            // 리다이렉션이 필요한 경우 (신규 회원)
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(response.redirectUrl()));
            return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
        } else {
            // 토큰 응답이 필요한 경우 (기존 회원)
            return ResponseEntity.ok(JSONResponse.onSuccess(response));
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