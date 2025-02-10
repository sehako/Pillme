package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.member.application.response.LoginMemberResponse;
import com.ssafy.pillme.member.application.service.LoginMemberService;
import com.ssafy.pillme.member.presentation.request.*;
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

    // 닉네임 중복 검증
    @PostMapping("/me/nickname/verify")
    public ResponseEntity<JSONResponse<Void>> verifyNickname(
            @RequestBody ChangeNicknameVerifyRequest request) {
        loginMemberService.validateNicknameChange(request.newNickname());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 비밀번호 변경
    @PostMapping("/me/password")
    public ResponseEntity<JSONResponse<Void>> updatePassword(
            @RequestBody UpdatePasswordRequest request) {
        loginMemberService.updatePassword(request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<JSONResponse<Void>> deleteMember() {
        loginMemberService.deleteMember();
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}