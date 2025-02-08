package com.ssafy.pillme.auth.presentation.controller;

import com.ssafy.pillme.auth.application.service.EmailService;
import com.ssafy.pillme.auth.presentation.request.SendEmailVerificationRequset;
import com.ssafy.pillme.auth.presentation.request.VerifyEmailRequest;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
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
    public ResponseEntity<JSONResponse<Void>> verifyEmail(
            @RequestBody VerifyEmailRequest request) {
        emailService.verifyEmail(request.email(), request.code());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
