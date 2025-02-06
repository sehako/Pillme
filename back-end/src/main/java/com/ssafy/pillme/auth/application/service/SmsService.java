package com.ssafy.pillme.auth.application.service;

import com.ssafy.pillme.auth.application.exception.external.FailedSmsDeliveryException;
import com.ssafy.pillme.auth.application.exception.verification.InvalidSmsCodeException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
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
@Slf4j
public class SmsService {
    private final RedisTemplate<String, String> redisTemplate;
    private DefaultMessageService messageService;

    @Value("${COOLSMS_API_KEY}")
    private String apiKey;

    @Value("${COOLSMS_API_SECRET}")
    private String apiSecret;

    @Value("${COOLSMS_SENDER_NUMBER}")
    private String senderNumber;

    private static final String SMS_CODE_PREFIX = "SMS:";
    private static final String VERIFIED_PREFIX = "VSMS:";
    private static final long CODE_EXPIRATION_TIME = 300000; // 5분
    private static final long VERIFIED_EXPIRATION_TIME = 600000; // 10분

    @PostConstruct
    public void setupMessageService() {
        if (apiKey == null || apiSecret == null) {
            throw new IllegalStateException("API 키와 시크릿이 설정되지 않았습니다.");
        }
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendVerificationSms(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("유효하지 않은 전화번호 형식입니다.");
        }

        String verificationCode = generateVerificationCode();
        saveVerificationCode(phoneNumber, verificationCode);

        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(phoneNumber);
        message.setText(String.format("[PillMe] 인증번호 [%s]를 입력해주세요.", verificationCode));

//        try {
//            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
//            if (!response.getStatusCode().equals("2000")) {
//                log.error("SMS 발송 실패: {}", response.getStatusMessage());
//                throw new IllegalArgumentException("SMS 발송에 실패했습니다: " + response.getStatusMessage());
//            }
//        } catch (NurigoMessageNotReceivedException e) {
//            log.error("메시지 전송 실패: {}", e.getMessage());
//            throw new FailedSmsDeliveryException("메시지 전송에 실패했습니다");
//        } catch (Exception e) {
//            log.error("예상치 못한 에러 발생: {}", e.getMessage());
//            throw new FailedSmsDeliveryException("SMS 서비스 오류가 발생했습니다");
//        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^01[0-9]{9}$");
    }

    public void verifySmsCode(String phoneNumber, String code) {
        String savedCode = findVerificationCode(phoneNumber);
        if (savedCode == null) {
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
