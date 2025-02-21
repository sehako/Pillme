package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.service.ChangeEmailService;
import com.ssafy.pillme.member.domain.vo.EmailValidationResult;
import com.ssafy.pillme.member.presentation.request.ChangeEmailRequest;
import com.ssafy.pillme.member.presentation.request.ChangeEmailVerificationRequest;
import com.ssafy.pillme.member.presentation.request.ChangeEmailVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class ChangeEmailController {
    private final ChangeEmailService emailService;
    private final ChangeEmailService changeEmailService;

    /**
     * 이메일 유효성 검증
     */
    @GetMapping("/check/email")
    public ResponseEntity<JSONResponse<EmailValidationResult>> validateEmail(
            @RequestParam String newEmail) {
        EmailValidationResult result = emailService.validateEmail(newEmail);
        return ResponseEntity.ok(JSONResponse.onSuccess(result));
    }

    /**
     * 이메일 변경 검증 및 인증메일 발송
     */
    @PostMapping("/email/verification")
    public ResponseEntity<JSONResponse<Void>> sendEmailVerification(
            @RequestBody ChangeEmailVerificationRequest request) {
        emailService.validateAndSendEmailVerification(request.newEmail());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 이메일 인증번호 확인
     */
    @PostMapping("/email/verify")
    public ResponseEntity<JSONResponse<Void>> verifyEmailCode(
            @RequestBody ChangeEmailVerifyRequest request) {
        emailService.verifyEmailCode(request.newEmail(), request.code());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 이메일 변경
     */
    @PutMapping("/email")
    public ResponseEntity<JSONResponse<Void>> changeEmail(
            @RequestBody ChangeEmailRequest request) {
        changeEmailService.changeEmail(request.newEmail());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
