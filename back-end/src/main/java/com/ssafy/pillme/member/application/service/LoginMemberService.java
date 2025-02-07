package com.ssafy.pillme.member.application.service;

import com.ssafy.pillme.auth.application.service.EmailService;
import com.ssafy.pillme.auth.application.service.SmsService;
import com.ssafy.pillme.member.application.exception.*;
import com.ssafy.pillme.member.domain.entity.LoginMember;
import com.ssafy.pillme.member.infrastructure.repository.LoginMemberRepository;
import com.ssafy.pillme.member.presentation.request.UpdateLoginMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginMemberService {
    private final LoginMemberRepository loginMemberRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    // 이메일 변경 프로세스
    public void validateAndSendEmailVerification(Long memberId, String newEmail) {
        // 기존 이메일과 중복 검증
        validateNewEmail(newEmail, memberId);
        // 인증 메일 발송
        emailService.sendVerificationEmail(newEmail);
    }

    // 이메일 인증 코드 검증
    public void verifyEmailCode(String email, String code) {
        emailService.verifyEmailCode(email, code);
        emailService.isVerified(email);
    }

    // 이메일 중복 및 현재값 검증
    private void validateNewEmail(String newEmail, Long memberId) {
        LoginMember member = loginMemberRepository.findById(memberId).orElseThrow(NoMemberInfoException::new);

        if (member.getEmail().equals(newEmail)) {
            throw new SameEmailException();
        }

        if(loginMemberRepository.existsByEmailAndDeletedFalse(newEmail)) {
            throw new AlreadyExistEmailAddressException();
        }
    }

    // 전화번호 변경 프로세스
    public void validateAndSendPhoneVerification(Long memberId, String newPhone) {
        // 기존 번호와 중복 검증
        validateNewPhone(newPhone, memberId);
        // 인증 SMS 발송
        smsService.sendVerificationSms(newPhone);
    }

    // 전화번호 인증 코드 검증
    public void verifySmsCode(String phone, String code) {
        smsService.verifySmsCode(phone, code);
        smsService.isVerified(phone);
    }

    // 전화번호 중복 및 현재값 검증
    private void validateNewPhone(String newPhone, Long memberId) {
        LoginMember member = loginMemberRepository.findById(memberId).orElseThrow(NoMemberInfoException::new);

        if (member.getPhone().equals(newPhone)) {
            throw new SamePhoneNumberException();
        }

        if(loginMemberRepository.existsByPhoneAndDeletedFalse(newPhone)) {
            throw new AlreadyExistPhoneNumberException();
        }
    }

    // 닉네임 변경 검증
    public void validateNicknameChange(Long memberId, String newNickname) {
        validateNewNickname(newNickname, memberId);
    }

    // 닉네임 중복 및 현재값 검증
    private void validateNewNickname(String newNickname, Long memberId) {
        LoginMember member = loginMemberRepository.findById(memberId).orElseThrow(NoMemberInfoException::new);

        if (member.getNickname().equals(newNickname)) {
            throw new SameNicknameException();
        }

        if(loginMemberRepository.existsByNicknameAndDeletedFalse(newNickname)) {
            throw new AlreadyExistNicknameException();
        }
    }

    // 최종 회원정보 업데이트
    public void updateMemberInformation(Long memberId, UpdateLoginMemberRequest request) {
        LoginMember member = loginMemberRepository.findById(memberId).orElseThrow(NoMemberInfoException::new);

        // 모든 검증이 통과되면 정보 업데이트
        member.updateInformation(request.email(), request.nickname(), request.phone());
    }
}
