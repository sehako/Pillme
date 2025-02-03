package com.ssafy.pillme.auth.application.service;

import com.ssafy.pillme.auth.application.exception.external.FailedSmsDeliveryException;
import com.ssafy.pillme.auth.application.exception.verification.InvalidSmsCodeException;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SmsService {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${COOLSMS_API_KEY}")
    private String apiKey;

    @Value("${COOLSMS_API_SECRET}")
    private String apiSecret;

    @Value("${COOLSMS_SENDER_NUMBER}")
    private String senderNumber;

    private static final String SMS_CODE_PREFIX = "SMS:";  // 인증번호 저장용
    private static final String VERIFIED_PREFIX = "VSMS:"; // 인증 완료 저장용
    private static final long CODE_EXPIRATION_TIME = 300000; // 5분
    private static final long VERIFIED_EXPIRATION_TIME = 600000; // 10분

    /**
     * 휴대전화 인증번호 발송
     */
    public void sendVerificationSms(String phoneNumber) {
        String verificationCode = generateVerificationCode();
        saveVerificationCode(phoneNumber, verificationCode);

        Message coolsms = new Message(apiKey, apiSecret);

        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNumber);
        params.put("from", senderNumber);
        params.put("type", "SMS");
        params.put("text", String.format("[PillMe] 인증번호 [%s]를 입력해주세요.", verificationCode));

        try {
            coolsms.send(params);
        } catch (CoolsmsException e) {
            throw new FailedSmsDeliveryException();
        }
    }

    /**
     * 인증번호 확인
     */
    public void verifySmsCode(String phoneNumber, String code) {
        String savedCode = findVerificationCode(phoneNumber);
        if (savedCode == null) {
            throw new FailedSmsDeliveryException();
        }
        if (!savedCode.equals(code)) {
            throw new InvalidSmsCodeException();
        }

        // 인증번호 삭제 및 인증 완료 상태 저장
        deleteVerificationCode(phoneNumber);
        saveVerifiedStatus(phoneNumber);
    }

    /**
     * 인증 완료 상태 확인
     */
    public boolean isVerified(String phoneNumber) {
        String key = VERIFIED_PREFIX + phoneNumber;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private void saveVerificationCode(String phoneNumber, String code) {
        String key = SMS_CODE_PREFIX + phoneNumber;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    private String findVerificationCode(String phoneNumber) {
        String key = SMS_CODE_PREFIX + phoneNumber;
        return redisTemplate.opsForValue().get(key);
    }

    private void deleteVerificationCode(String phoneNumber) {
        String key = SMS_CODE_PREFIX + phoneNumber;
        redisTemplate.delete(key);
    }

    private void saveVerifiedStatus(String phoneNumber) {
        String key = VERIFIED_PREFIX + phoneNumber;
        redisTemplate.opsForValue().set(key, "true", VERIFIED_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }
}