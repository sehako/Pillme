package com.americanstartup.pillme.auth.presentation.controller;

import com.americanstartup.pillme.auth.application.service.EmailService;
import com.americanstartup.pillme.auth.application.service.TokenService;
import com.americanstartup.pillme.auth.presentation.request.SendEmailVerificationRequset;
import com.americanstartup.pillme.auth.presentation.request.VerifyEmailRequest;
import com.americanstartup.pillme.global.response.JSONResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
