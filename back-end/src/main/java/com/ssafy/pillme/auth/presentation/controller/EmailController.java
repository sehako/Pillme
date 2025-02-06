package com.ssafy.pillme.auth.presentation.controller;

import com.ssafy.pillme.auth.application.service.EmailService;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam String email) {
        emailService.sendVerificationEmail(email);
        return ResponseEntity.ok(JSONResponse.onSuccess(null));
    }

    /**
     * 이메일 인증번호 확인
     */
    @PostMapping("/email/verify")
    public ResponseEntity<JSONResponse<Void>> verifyEmail(
            @RequestParam String email,
            @RequestParam String code) {
        emailService.verifyEmail(email, code);
        return ResponseEntity.ok(JSONResponse.onSuccess(null));
    }
}
