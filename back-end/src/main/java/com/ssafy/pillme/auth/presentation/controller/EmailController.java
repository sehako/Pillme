package com.ssafy.pillme.auth.presentation.controller;

import com.ssafy.pillme.auth.application.service.EmailService;
import com.ssafy.pillme.auth.application.service.TokenService;
import com.ssafy.pillme.auth.presentation.request. PasswordResetEmailCheckRequest;
import com.ssafy.pillme.auth.presentation.request.SendEmailVerificationRequset;
import com.ssafy.pillme.auth.presentation.request.VerifyEmailRequest;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final TokenService tokenService;

    /**
     * 이메일 인증번호 발송
     */
    @PostMapping("/email/verification")
    public ResponseEntity<JSONResponse<Void>> sendEmailVerification(
            @RequestBody SendEmailVerificationRequset requset) {
        emailService.sendVerificationEmail(requset.email());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 이메일 인증번호 확인
     */
    @PostMapping("/email/verify")
    public ResponseEntity<JSONResponse<Void>> verifyEmailCode(
            @RequestBody VerifyEmailRequest request) {
        emailService.verifyEmailCode(request.email(), request.code());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 비밀번호 재설정 이메일 발송
     */
    @PostMapping("/password/reset-request")
    public ResponseEntity<JSONResponse<Void>> sendPasswordResetEmail(
            @RequestBody PasswordResetEmailCheckRequest request) {
        emailService.sendPasswordResetEmail(request.email(), tokenService.generateResetToken(request.email()));
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
