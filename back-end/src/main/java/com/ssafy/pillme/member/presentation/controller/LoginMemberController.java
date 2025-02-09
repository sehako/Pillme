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
    @GetMapping("/{memberId}")
    public ResponseEntity<JSONResponse<LoginMemberResponse>> findMemberProfile(
            @PathVariable Long memberId) {
        LoginMemberResponse profile = loginMemberService.findMemberProfile(memberId);
        return ResponseEntity.ok(JSONResponse.onSuccess(profile));
    }

    // 닉네임 중복 검증
    @PostMapping("/{memberId}/nickname/verify")
    public ResponseEntity<JSONResponse<Void>> verifyNickname(
            @PathVariable Long memberId,
            @RequestBody ChangeNicknameVerifyRequest request) {
        loginMemberService.validateNicknameChange(memberId, request.newNickname());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 최종 정보 업데이트
    // 프론트와 협의 후 제거 예정
    @PostMapping("/{memberId}/update")
    public ResponseEntity<JSONResponse<Void>> updateMemberInfo(@PathVariable Long memberId, @RequestBody UpdateLoginMemberRequest request) {
        loginMemberService.updateMemberInformation(memberId, request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 비밀번호 변경
    @PostMapping("/{memberId}/password")
    public ResponseEntity<JSONResponse<Void>> updatePassword(
            @PathVariable Long memberId,
            @RequestBody UpdatePasswordRequest request) {
        loginMemberService.updatePassword(memberId, request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 회원 탈퇴
    @DeleteMapping("/{memberId}")
    public ResponseEntity<JSONResponse<Void>> deleteMember(
            @PathVariable Long memberId) {
        loginMemberService.deleteMember(memberId);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}