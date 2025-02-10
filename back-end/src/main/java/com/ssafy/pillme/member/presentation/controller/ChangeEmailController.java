package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.service.ChangeEmailService;
import com.ssafy.pillme.member.presentation.request.ChangeEmailVerificationRequest;
import com.ssafy.pillme.member.presentation.request.ChangeEmailVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ChangeEmailController {
    private final ChangeEmailService emailService;

    /**
     * 이메일 변경 검증 및 인증메일 발송
     */
    @PostMapping("/{memberId}/email/verification")
    public ResponseEntity<JSONResponse<Void>> sendEmailVerification(
            @PathVariable Long memberId,
            @RequestBody ChangeEmailVerificationRequest request) {
        emailService.validateAndSendEmailVerification(memberId, request.newEmail());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 이메일 인증번호 확인
     */
    @PostMapping("/{memberId}/email/verify")
    public ResponseEntity<JSONResponse<Void>> verifyEmailCode(
            @RequestBody ChangeEmailVerifyRequest request) {
        emailService.verifyEmailCode(request.newEmail(), request.code());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
