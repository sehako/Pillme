package com.americanstartup.pillme.member.application.service;

import com.americanstartup.pillme.global.util.SecurityUtil;
import com.americanstartup.pillme.member.application.exception.InvalidNewPasswordFormatException;
import com.americanstartup.pillme.member.application.exception.InvalidPasswordException;
import com.americanstartup.pillme.member.application.exception.NoMemberInfoException;
import com.americanstartup.pillme.member.domain.entity.LoginMember;
import com.americanstartup.pillme.member.infrastructure.repository.LoginMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChangePasswordService {
    private final LoginMemberRepository loginMemberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 비밀번호 변경
     */
    @Transactional
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
    }

    /**
     * 현재 비밀번호 검증
     */
    public boolean validateCurrentPassword(String currentPassword) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);

        return passwordEncoder.matches(currentPassword, member.getPassword());
    }

    /**
     * 새 비밀번호 유효성 검증
     */
    public boolean validateNewPasswordFormat(String newPassword) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?])[A-Za-z\\d~`!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?]{12,300}$";
        return Pattern.matches(passwordRegex, newPassword);
    }
}
