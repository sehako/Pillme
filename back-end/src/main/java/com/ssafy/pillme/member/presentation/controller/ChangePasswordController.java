package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.service.ChangePasswordService;
import com.ssafy.pillme.member.domain.vo.PasswordValidationResult;
import com.ssafy.pillme.member.presentation.request.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class ChangePasswordController {
    private final ChangePasswordService changePasswordService;

    @GetMapping("/check/password")
    public ResponseEntity<JSONResponse<PasswordValidationResult>> verifyPassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        PasswordValidationResult result =
                changePasswordService.validatePasswordChange(currentPassword, newPassword);
        return ResponseEntity.ok(JSONResponse.onSuccess(result));
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<JSONResponse<Void>> changePassword(
            @RequestBody ChangePasswordRequest request) {
        changePasswordService.changePassword(request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
