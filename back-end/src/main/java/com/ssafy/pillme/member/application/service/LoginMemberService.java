package com.ssafy.pillme.member.application.service;

import com.ssafy.pillme.global.util.SecurityUtil;
import com.ssafy.pillme.member.application.exception.*;
import com.ssafy.pillme.member.application.response.LoginMemberResponse;
import com.ssafy.pillme.member.domain.entity.LoginMember;
import com.ssafy.pillme.member.infrastructure.repository.LoginMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginMemberService {
    private final LoginMemberRepository loginMemberRepository;
    private final PasswordEncoder passwordEncoder;

    // 현재 로그인한 멤버 프로필 조회
    public LoginMemberResponse findCurrentMemberProfile() {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);
        return LoginMemberResponse.from(member);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteMember() {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);

        member.delete();
    }
}
