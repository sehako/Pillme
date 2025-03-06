package com.americanstartup.pillme.member.presentation.controller;

import com.americanstartup.pillme.global.response.JSONResponse;
import com.americanstartup.pillme.member.application.service.ChangePhoneService;
import com.americanstartup.pillme.member.domain.vo.PhoneValidationResult;
import com.americanstartup.pillme.member.presentation.request.ChangePhoneRequest;
import com.americanstartup.pillme.member.presentation.request.ChangePhoneVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class ChangePhoneController {
    private final ChangePhoneService changePhoneService;

    /**
     * 전화번호 유효성 검증
     */
    @GetMapping("/check/phone")
    public ResponseEntity<JSONResponse<PhoneValidationResult>> validatePhone(
            @RequestParam String newPhoneNumber) {
        PhoneValidationResult result = changePhoneService.validatePhone(newPhoneNumber);
        return ResponseEntity.ok(JSONResponse.onSuccess(result));
    }

    /**
     * 전화번호 변경을 위한 인증번호 발송
     */
    @PostMapping("/phone/verification")
    public ResponseEntity<JSONResponse<Void>> sendChangePhoneVerification(
            @RequestBody ChangePhoneVerifyRequest request) {
        changePhoneService.validateAndSendPhoneVerification(request.newPhoneNumber());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 전화번호 변경 인증번호 확인
     */
    @PostMapping("/phone/verify")
    public ResponseEntity<JSONResponse<Void>> verifyChangePhoneCode(
            @RequestBody ChangePhoneVerifyRequest request) {
        changePhoneService.verifySmsCode(request.newPhoneNumber(), request.code());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 전화번호 변경
     */
    @PutMapping("/phone")
    public ResponseEntity<JSONResponse<Void>> changePhone(
            @RequestBody ChangePhoneRequest request) {
        changePhoneService.changePhone(request.newPhoneNumber());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}