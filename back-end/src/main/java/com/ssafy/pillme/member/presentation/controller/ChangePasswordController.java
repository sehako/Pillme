package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.service.ChangeNicknameService;
import com.ssafy.pillme.member.application.service.ChangePasswordService;
import com.ssafy.pillme.member.presentation.request.ChangeNicknameRequest;
import com.ssafy.pillme.member.presentation.request.ChangeNicknameVerifyRequest;
import com.ssafy.pillme.member.presentation.request.UpdatePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class ChangePasswordController {
    private final ChangePasswordService changePasswordService;

    // 비밀번호 변경
    @PostMapping("/password")
    public ResponseEntity<JSONResponse<Void>> updatePassword(
            @RequestBody UpdatePasswordRequest request) {
        changePasswordService.updatePassword(request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
