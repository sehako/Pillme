package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.service.ChangePhoneService;
import com.ssafy.pillme.member.presentation.request.ChangePhoneRequest;
import com.ssafy.pillme.member.presentation.request.ChangePhoneVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class ChangePhoneController {
    private final ChangePhoneService changePhoneService;

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

    @PutMapping("/phone")
    public ResponseEntity<JSONResponse<Void>> changePhone(
            @RequestBody ChangePhoneRequest request) {
        changePhoneService.changePhone(request.newPhoneNumber());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
