package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.service.ChangePhoneService;
import com.ssafy.pillme.member.presentation.request.ChangePhoneVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ChangePhoneController {
    private final ChangePhoneService changePhoneService;

    /**
     * 전화번호 변경을 위한 인증번호 발송
     */
    @PostMapping("/{memberId}/phone/verification")
    public ResponseEntity<JSONResponse<Void>> sendChangePhoneVerification(
            @PathVariable Long memberId,
            @RequestBody ChangePhoneVerifyRequest request) {
        changePhoneService.validateAndSendPhoneVerification(memberId, request.newPhoneNumber());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /**
     * 전화번호 변경 인증번호 확인
     */
    @PostMapping("/{memberId}/phone/verify")
    public ResponseEntity<JSONResponse<Void>> verifyChangePhoneCode(
            @PathVariable Long memberId,
            @RequestBody ChangePhoneVerifyRequest request) {
        changePhoneService.verifySmsCode(request.newPhoneNumber(), request.code());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
