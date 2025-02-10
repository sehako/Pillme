package com.ssafy.pillme.member.application.service;

import com.ssafy.pillme.global.util.SecurityUtil;
import com.ssafy.pillme.member.application.exception.*;
import com.ssafy.pillme.member.application.response.LoginMemberResponse;
import com.ssafy.pillme.member.domain.entity.LoginMember;
import com.ssafy.pillme.member.infrastructure.repository.LoginMemberRepository;
import com.ssafy.pillme.member.presentation.request.UpdateLoginMemberRequest;
import com.ssafy.pillme.member.presentation.request.UpdatePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Transactional
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

    // 닉네임 변경 검증
    public void validateNicknameChange(String newNickname) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        validateNewNickname(newNickname, currentMemberId);
    }

    // 닉네임 중복 및 현재값 검증
    private void validateNewNickname(String newNickname, Long memberId) {
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(memberId).orElseThrow(NoMemberInfoException::new);

        if (member.getNickname().equals(newNickname)) {
            throw new SameNicknameException();
        }

        if(loginMemberRepository.existsByNicknameAndDeletedFalse(newNickname)) {
            throw new AlreadyExistNicknameException();
        }
    }

    // 비밀번호 변경
    public void updatePassword( UpdatePasswordRequest request) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId).orElseThrow(NoMemberInfoException::new);

        validateCurrentPassword(request.currentPassword(), currentMemberId);
        validateNewPassword(request.newPassword());

        String encodedPassword = passwordEncoder.encode(request.newPassword());
        member.updatePassword(encodedPassword);
        loginMemberRepository.save(member);
    }

    /**
     * 현재 비밀번호 검증
     */
    private void validateCurrentPassword(String currentPassword, Long memberId) {
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(NoMemberInfoException::new);

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new InvalidPasswordException();
        }
    }

    /**
     * 새 비밀번호 유효성 검증
     */
    private void validateNewPassword(String newPassword) {
        // 비밀번호 정규식 패턴 - 대문자, 소문자, 숫자, 특수문자 포함, 정확히 12자
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?])[A-Za-z\\d~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?]{12}$";

        try {
            if (!Pattern.matches(passwordRegex, newPassword)) {
                throw new InvalidNewPasswordFormatException();
            }
        } catch (Exception e) {
            throw new InvalidNewPasswordFormatException();
        }
    }

    // 회원 탈퇴
    public void deleteMember() {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);

        member.delete();
        loginMemberRepository.save(member);
    }
}
