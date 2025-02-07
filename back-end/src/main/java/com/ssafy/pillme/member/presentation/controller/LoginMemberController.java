package com.ssafy.pillme.member.presentation.controller;

import com.ssafy.pillme.member.application.service.LoginMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class LoginMemberController {
    private final LoginMemberService loginMemberService;

    // 이메일 변경 검증 및 인증메일 발송
    @PostMapping("/{memberId}/email/verify")
    public ResponseEntity<Void> verifyAndSendEmail(
            @PathVariable Long memberId,
            @RequestBody String newEmail) {
        loginMemberService.validateAndSendEmailVerification(memberId, newEmail);
        return ResponseEntity.ok().build();
    }

    // 이메일 인증 코드 확인
    @PostMapping("/{memberId}/email/verify-code")
    public ResponseEntity<Boolean> verifyEmailCode(
            @RequestBody EmailVerificationRequest request) {
        boolean verified = loginMemberService.verifyEmailCode(
                request.getEmail(), request.getCode());
        return ResponseEntity.ok(verified);
    }

    // 전화번호 변경 검증 및 인증SMS 발송
    @PostMapping("/{memberId}/phone/verify")
    public ResponseEntity<Void> verifyAndSendPhone(
            @PathVariable Long memberId,
            @RequestBody String newPhone) {
        loginMemberService.validateAndSendPhoneVerification(memberId, newPhone);
        return ResponseEntity.ok().build();
    }

    // 전화번호 인증 코드 확인
    @PostMapping("/{memberId}/phone/verify-code")
    public ResponseEntity<Boolean> verifyPhoneCode(
            @RequestBody PhoneVerificationRequest request) {
        boolean verified = loginMemberService.verifyPhoneCode(
                request.getPhone(), request.getCode());
        return ResponseEntity.ok(verified);
    }

    // 닉네임 중복 검증
    @PostMapping("/{memberId}/nickname/verify")
    public ResponseEntity<Void> verifyNickname(
            @PathVariable Long memberId,
            @RequestBody String newNickname) {
        loginMemberService.validateNicknameChange(memberId, newNickname);
        return ResponseEntity.ok().build();
    }

    // 최종 정보 업데이트
    @PostMapping("/{memberId}/email/verify")
    public ResponseEntity<Void> updateMemberInfo(@PathVariable Long memberId, @RequestBody MemberUpdateRequest request) {
        loginMemberService.updateMemberInformation(memberId, request);
        return ResponseEntity.ok().build();
    }
}