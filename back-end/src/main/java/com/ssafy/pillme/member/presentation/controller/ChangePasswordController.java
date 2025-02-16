package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.service.ChangePasswordService;
import com.ssafy.pillme.member.presentation.request.ChangePasswordRequest;
import com.ssafy.pillme.member.presentation.request.PasswordValidationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class ChangePasswordController {
    private final ChangePasswordService changePasswordService;

    // 현재 로그인한 멤버 비밀번호 조회
    @PostMapping("/password/verify")
    public ResponseEntity<JSONResponse<Boolean>> checkCurrentPassword(
            @RequestBody PasswordValidationRequest request) {
        boolean isValid = changePasswordService.validateCurrentPassword(request.password());
        return ResponseEntity.ok(JSONResponse.onSuccess(isValid));
    }

    // 새로운 비밀번호 유효성 검증
    @PostMapping("/check/password")
    public ResponseEntity<JSONResponse<Boolean>> validateNewPassword(
            @RequestBody PasswordValidationRequest request) {
        boolean isValid = changePasswordService.validateNewPasswordFormat(request.password());
        return ResponseEntity.ok(JSONResponse.onSuccess(isValid));
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<JSONResponse<Void>> changePassword(
            @RequestBody ChangePasswordRequest request) {
        changePasswordService.changePassword(request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
