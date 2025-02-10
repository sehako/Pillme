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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<JSONResponse<OAuth2Response>> googleCallback(
            @RequestParam String code) {
        OAuth2Response response = oauth2Service.googleLogin(code);
        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    }

    /**
     * OAuth2 추가 회원정보 입력
     */
    @PutMapping("/additional-info")
    public ResponseEntity<JSONResponse<MemberResponse>> submitAdditionalInfo(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody OAuthAdditionalInfoRequest request) {
        MemberResponse response = authService.submitAdditionalInfo(memberId, request);
        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    }

    /**
     * OAuth2 회원가입 완료
     */
    @PostMapping("/signup")
    public ResponseEntity<JSONResponse<TokenResponse>> oauthSignUp(
            @Valid @RequestBody OAuthSignUpRequest request,
            @RequestParam String provider) {
        TokenResponse response = authService.oauthSignUp(request, provider);
        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    }
}