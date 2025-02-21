package com.ssafy.pillme.auth.presentation.controller;

import com.ssafy.pillme.auth.application.response.FindEmailResponse;
import com.ssafy.pillme.auth.application.response.MemberResponse;
import com.ssafy.pillme.auth.application.response.TokenResponse;
import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.vo.Role;
import com.ssafy.pillme.auth.infrastructure.repository.MemberRepository;
import com.ssafy.pillme.auth.presentation.request.LoginRequest;
import com.ssafy.pillme.auth.presentation.request.SendEmailVerificationRequset;
import com.ssafy.pillme.auth.presentation.request.SignUpRequest;
import com.ssafy.pillme.auth.presentation.request.PasswordResetEmailSendRequest;
import com.ssafy.pillme.global.response.JSONResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<JSONResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request) {
        MemberResponse response = authService.signUp(request);
        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<JSONResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<JSONResponse<TokenResponse>> refreshToken(
            @RequestHeader("refreshToken") String refreshToken) {
        TokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<JSONResponse<Void>> logout(
            @RequestHeader("Authorization") String bearerToken) {

        String accessToken = bearerToken.substring(7);
        authService.logout(accessToken);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 비밀번호 유효성 검증
     */
    @PostMapping("/check/password")
    public ResponseEntity<JSONResponse<Boolean>> checkPasswordValid(
            @RequestParam String password) {
        boolean isValid = authService.validatePassword(password);
        return ResponseEntity.ok(JSONResponse.onSuccess(isValid));
    }

    /**
     * 닉네임 중복 검사
     */
    @GetMapping("/check/nickname")
    public ResponseEntity<JSONResponse<Boolean>> checkNicknameDuplicate(
            @RequestParam String nickname) {
        boolean isDuplicate = authService.checkNicknameDuplicate(nickname);
        return ResponseEntity.ok(JSONResponse.onSuccess(isDuplicate));
    }

    /**
     * 이메일 찾기
     */
    @GetMapping("/find-email")
    public ResponseEntity<JSONResponse<FindEmailResponse>> findEmail(
            @RequestParam String phone) {
        FindEmailResponse response = authService.findEmail(phone);
        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    }

    /**
     * 비밀번호 재설정을 위한 이메일 인증 요청
     */
    @PostMapping("/temporary-password/verify-email")
    public ResponseEntity<JSONResponse<Void>> requestEmailVerification(
            @RequestBody SendEmailVerificationRequset request) {
        authService.verifyEmailForPasswordReset(request.email());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 임시 비밀번호 발급 요청
     */
    @PostMapping("/temporary-password/request")
    public ResponseEntity<JSONResponse<Void>> requestTemporaryPassword(
            @RequestBody PasswordResetEmailSendRequest request) {
        authService.sendTemporaryPasswordEmail(request.email(), request.phone());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    @GetMapping("/check/email")
    public ResponseEntity<JSONResponse<Boolean>> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = memberRepository.existsByEmailAndRoleNotAndDeletedFalse(email, Role.LOCAL);
        return ResponseEntity.ok(JSONResponse.onSuccess(isDuplicate));
    }

    @GetMapping("/check/phone")
    public ResponseEntity<JSONResponse<Boolean>> checkPhoneDuplicate(@RequestParam String phone) {
        boolean isDuplicate = memberRepository.existsByPhoneAndDeletedFalse(phone);
        return ResponseEntity.ok(JSONResponse.onSuccess(isDuplicate));
    }
}