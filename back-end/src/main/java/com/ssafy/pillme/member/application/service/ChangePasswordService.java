package com.ssafy.pillme.member.application.service;

import com.ssafy.pillme.global.util.SecurityUtil;
import com.ssafy.pillme.member.application.exception.*;
import com.ssafy.pillme.member.domain.entity.LoginMember;
import com.ssafy.pillme.member.domain.vo.PasswordValidationResult;
import com.ssafy.pillme.member.infrastructure.repository.LoginMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {
    private final LoginMemberRepository loginMemberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 비밀번호 변경
     */
    public void changePassword(String currentPassword, String newPassword) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (!validateNewPasswordFormat(newPassword)) {
            throw new InvalidNewPasswordFormatException();
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        member.updatePassword(encodedPassword);
        loginMemberRepository.save(member);
    }

    /**
     * 비밀번호 검증
     */
    public PasswordValidationResult validatePasswordChange(String currentPassword, String newPassword) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);

        boolean isCurrentPasswordValid = passwordEncoder.matches(currentPassword, member.getPassword());
        boolean isNewPasswordValid = validateNewPasswordFormat(newPassword);

        return new PasswordValidationResult(isCurrentPasswordValid, isNewPasswordValid);
    }

    /**
     * 현재 비밀번호 검증
     */
    private void validateNewPasswordFormat(String currentPassword, Long memberId) {
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(NoMemberInfoException::new);

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new InvalidPasswordException();
        }
    }

    /**
     * 새 비밀번호 유효성 검증
     */
    private boolean validateNewPasswordFormat(String newPassword) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?])[A-Za-z\\d~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?]{12}$";
        return Pattern.matches(passwordRegex, newPassword);
    }
}
