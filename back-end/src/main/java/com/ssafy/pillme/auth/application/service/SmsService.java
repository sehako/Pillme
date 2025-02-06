package com.ssafy.pillme.auth.application.service;

import com.ssafy.pillme.auth.application.exception.sms.*;
import com.ssafy.pillme.auth.application.exception.validation.InvalidPhoneNumberException;
import com.ssafy.pillme.auth.application.exception.verification.ExpiredSmsCodeException;
import com.ssafy.pillme.auth.application.exception.verification.InvalidSmsCodeException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SmsService {
    @Value("${COOLSMS_API_KEY}")
    private String apiKey;

    @Value("${COOLSMS_API_SECRET}")
    private String apiSecret;

    @Value("${COOLSMS_SENDER_NUMBER}")
    private String senderNumber;

    private DefaultMessageService messageService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SMS_CODE_PREFIX = "SMS:";
    private static final String VERIFIED_PREFIX = "VSMS:";
    private static final long CODE_EXPIRATION_TIME = 300000; // 5분
    private static final long VERIFIED_EXPIRATION_TIME = 1800000; // 30분

    @PostConstruct
    public void setupMessageService() {
        if (apiKey == null || apiSecret == null) {
            throw new InvalidSmsApiKeyException();
        }
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendVerificationSms(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new InvalidPhoneNumberException();
        }

        String verificationCode = generateVerificationCode();
        saveVerificationCode(phoneNumber, verificationCode);

        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(phoneNumber);
        message.setText(String.format("[PillMe]\n인증번호: %s\n인증번호는 5분간 유효합니다.", verificationCode));

        try {
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            if (!response.getStatusCode().equals("2000")) {
                throw new FailedSmsDeliveryException();
            }
            return response;
        } catch (Exception e) {
            throw new FailedSmsDeliveryException();
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^01[0-9]{9}$");
    }

    public void verifySmsCode(String phoneNumber, String code) {
        String savedCode = findVerificationCode(phoneNumber);
        if (savedCode == null) {
            Long ttl = redisTemplate.getExpire(SMS_CODE_PREFIX + phoneNumber);
            if (ttl != null && ttl <= 0) {
                throw new ExpiredSmsCodeException();
            }
            throw new FailedSmsDeliveryException();
        }
        if (!savedCode.equals(code)) {
            throw new InvalidSmsCodeException();
        }

        deleteVerificationCode(phoneNumber);
        saveVerifiedStatus(phoneNumber);
    }

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
