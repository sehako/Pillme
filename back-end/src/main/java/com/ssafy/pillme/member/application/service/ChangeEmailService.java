package com.ssafy.pillme.member.application.service;

import com.ssafy.pillme.global.util.SecurityUtil;
import com.ssafy.pillme.member.application.exception.*;
import com.ssafy.pillme.member.domain.entity.LoginMember;
import com.ssafy.pillme.member.domain.vo.EmailValidationResult;
import com.ssafy.pillme.member.infrastructure.repository.LoginMemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChangeEmailService {
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final LoginMemberRepository loginMemberRepository;

    @Value("${EMAIL_SENDER}")
    private String sender;

    private static final String EMAIL_CODE_PREFIX = "EC:";    // 인증번호 저장용
    private static final String VERIFIED_PREFIX = "VE:";      // 인증 완료 저장용
    private static final long CODE_EXPIRATION_TIME = 300000;  // 5분
    private static final long VERIFIED_EXPIRATION_TIME = 1800000; // 30분

    /**
     * 이메일 유효성 검증
     */
    public EmailValidationResult validateEmail(String newEmail) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        LoginMember member = loginMemberRepository.findById(currentMemberId)
                .orElseThrow(NoMemberInfoException::new);

        boolean isSameAsCurrent = member.getEmail().equals(newEmail);
        boolean isAlreadyExists = loginMemberRepository.existsByEmailAndDeletedFalse(newEmail);

        return new EmailValidationResult(isSameAsCurrent, isAlreadyExists);
    }


    /**
     * 이메일 변경 검증 및 인증번호 발송
     */
    public void validateAndSendEmailVerification(String newEmail) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();
        // 기존 이메일과 중복 검증
        validateNewEmail(newEmail, currentMemberId);
        // 인증 메일 발송
        sendVerificationEmail(newEmail);
    }

    /**
     * 이메일 변경
     */
    public void changeEmail(String newEmail) {
        Long currentMemberId = SecurityUtil.extractCurrentMemberId();

        if(!isVerified(newEmail)) {
            throw new NotVerifiedEmailAddressException();
        }

        // 회원 조회 및 이메일 변경
        LoginMember member = loginMemberRepository.findByIdAndDeletedFalse(currentMemberId).orElseThrow(NoMemberInfoException::new);

        member.updateEmailAddress(newEmail);
        loginMemberRepository.save(member);
    }

    /**
     * 이메일 중복 및 현재값 검증
     */
    private void validateNewEmail(String newEmail, Long memberId) {
        LoginMember member = loginMemberRepository.findById(memberId)
                .orElseThrow(NoMemberInfoException::new);

        if (member.getEmail().equals(newEmail)) {
            throw new SameEmailException();
        }

        if(loginMemberRepository.existsByEmailAndDeletedFalse(newEmail)) {
            throw new AlreadyExistEmailAddressException();
        }
    }

    /**
     * 이메일 변경 인증번호 발송
     */
    private void sendVerificationEmail(String to) {
        String code = generateVerificationCode();
        saveVerificationCode(to, code);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject("[Pillme] 이메일 인증");
            helper.setText(createChangeEmailVerificationEmailContent(code), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new FailedEmailSendException();
        }
    }

    /**
     * 이메일 인증번호 확인
     */
    public void verifyEmailCode(String email, String code) {
        String savedCode = getVerificationCode(email);
        if (savedCode == null) {
            throw new ExpiredChagneEmailCodeException();
        }
        if (!savedCode.equals(code)) {
            throw new InvalidChangeEmailCodeException();
        }

        // 인증번호 삭제 및 인증 완료 상태 저장
        deleteVerificationCode(email);
        saveVerifiedStatus(email);
    }

    /**
     * 이메일 인증 완료 상태 확인
     */
    private boolean isVerified(String email) {
        String key = VERIFIED_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private void saveVerificationCode(String email, String code) {
        String key = EMAIL_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    private String getVerificationCode(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        return redisTemplate.opsForValue().get(key);
    }

    private void deleteVerificationCode(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        redisTemplate.delete(key);
    }

    private void saveVerifiedStatus(String email) {
        String key = VERIFIED_PREFIX + email;
        redisTemplate.opsForValue().set(key, "true", VERIFIED_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    private String createChangeEmailVerificationEmailContent(String code) {
        return String.format("""
            <div style='margin:100px;'>
                <h1>이메일 인증번호</h1>
                <p>아래의 인증번호를 입력해주세요:</p>
                <div style='font-size:130%%'>인증번호: <strong>%s</strong></div>
                <p>이 인증번호는 5분 후에 만료됩니다.</p>
            </div>
            """, code);
    }
}