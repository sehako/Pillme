package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.response.LoginMemberResponse;
import com.ssafy.pillme.member.application.service.LoginMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class LoginMemberController {
    private final LoginMemberService loginMemberService;

    // 멤버 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<JSONResponse<LoginMemberResponse>> findMemberProfile() {
        LoginMemberResponse profile = loginMemberService.findCurrentMemberProfile();
        return ResponseEntity.ok(JSONResponse.onSuccess(profile));
    }

    // 현재 로그인한 멤버 비밀번호 조회
    @GetMapping("/me/password")
    public ResponseEntity<JSONResponse<Void>> checkCurrentPassword(@RequestParam String currentPassword) {
        loginMemberService.validateCurrentPassword(currentPassword);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<JSONResponse<Void>> deleteMember() {
        loginMemberService.deleteMember();
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}