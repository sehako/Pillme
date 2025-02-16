package com.ssafy.pillme.auth.application.service;

import com.ssafy.pillme.auth.application.exception.email.FailedEmailDeliveryException;
import com.ssafy.pillme.auth.application.exception.verification.ExpiredEmailCodeException;
import com.ssafy.pillme.auth.application.exception.verification.InvalidEmailCodeException;
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
public class EmailService {
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${EMAIL_SENDER}")
    private String sender;

    private static final String EMAIL_CODE_PREFIX = "EC:";    // 인증번호 저장용
    private static final String VERIFIED_PREFIX = "VE:";      // 인증 완료 저장용
    private static final long CODE_EXPIRATION_TIME = 300000;  // 5분
    private static final long VERIFIED_EXPIRATION_TIME = 1800000; // 30분

    /**
     * 이메일 인증번호 발송
     */
    public void sendVerificationEmail(String to) {
        String code = generateVerificationCode();
        saveVerificationCode(to, code);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject("[Pillme] 이메일 인증");
            helper.setText(createVerificationEmailContent(code), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new FailedEmailDeliveryException();
        }
    }

    /**
     * 이메일 인증번호 확인
     */
    public void verifyEmailCode(String email, String code) {
        String savedCode = getVerificationCode(email);
        if (savedCode == null) {
            throw new ExpiredEmailCodeException();
        }
        if (!savedCode.equals(code)) {
            throw new InvalidEmailCodeException();
        }

        // 인증번호 삭제 및 인증 완료 상태 저장
        deleteVerificationCode(email);
        saveVerifiedStatus(email);
    }

    /**
     * 임시 비밀번호 생성 및 이메일 발송
     */
    public String sendTemporaryPassword(String email, String temporaryPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(sender);
            helper.setTo(email);
            helper.setSubject("[Pillme] 임시 비밀번호 안내");
            helper.setText(createTemporaryPasswordEmailContent(temporaryPassword), true);

            mailSender.send(message);
            return temporaryPassword;
        } catch (MessagingException e) {
            throw new FailedEmailDeliveryException();
        }
    }

    /**
     * 이메일 인증 완료 상태 확인
     */
    public boolean isVerified(String email) {
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

    private String createVerificationEmailContent(String code) {
        return String.format("""
            <div style='margin:100px;'>
                <h1>이메일 인증번호</h1>
                <p>아래의 인증번호를 입력해주세요:</p>
                <div style='font-size:130%%'>인증번호: <strong>%s</strong></div>
                <p>이 인증번호는 5분 후에 만료됩니다.</p>
            </div>
            """, code);
    }

    private String createTemporaryPasswordEmailContent(String password) {
        return String.format("""
            <div style='margin:100px;'>
                <h1>임시 비밀번호 안내</h1>
                <p>회원님의 임시 비밀번호가 발급되었습니다.</p>
                <div style='font-size:130%%'>임시 비밀번호: <strong>%s</strong></div>
                <p>보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.</p>
            </div>
            """, password);
    }
}