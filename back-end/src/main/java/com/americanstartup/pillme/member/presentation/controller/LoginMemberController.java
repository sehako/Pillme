package com.americanstartup.pillme.member.presentation.controller;

import com.americanstartup.pillme.global.response.JSONResponse;
import com.americanstartup.pillme.member.application.response.LoginMemberResponse;
import com.americanstartup.pillme.member.application.service.LoginMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class LoginMemberController {
    private final LoginMemberService loginMemberService;

    // 멤버 프로필 조회
    @GetMapping()
    public ResponseEntity<JSONResponse<LoginMemberResponse>> findMemberProfile() {
        LoginMemberResponse profile = loginMemberService.findCurrentMemberProfile();
        return ResponseEntity.ok(JSONResponse.onSuccess(profile));
    }

    // 회원 탈퇴
    @DeleteMapping()
    public ResponseEntity<JSONResponse<Void>> deleteMember() {
        loginMemberService.deleteMember();
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}