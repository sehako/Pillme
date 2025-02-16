package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.service.ChangePasswordService;
import com.ssafy.pillme.member.presentation.request.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class ChangePasswordController {
    private final ChangePasswordService changePasswordService;

    // 비밀번호 변경
    @GetMapping("/check/password")
    public ResponseEntity<JSONResponse<Boolean>> validateNewPassword(
            @RequestParam String newPassword) {
        boolean isValid = changePasswordService.validateNewPasswordFormat(newPassword);
        return ResponseEntity.ok(JSONResponse.onSuccess(isValid));
    }

    // 현재 로그인한 멤버 비밀번호 조회
    @GetMapping("/password")
    public ResponseEntity<JSONResponse<Boolean>> checkCurrentPassword(@RequestParam String currentPassword) {
        boolean isValid = changePasswordService.validateCurrentPassword(currentPassword);
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
