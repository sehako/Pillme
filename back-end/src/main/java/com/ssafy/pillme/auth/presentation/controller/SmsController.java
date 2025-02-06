package com.ssafy.pillme.auth.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j

public class SmsController {

    @Value("${COOLSMS_API_KEY}")
    private String apiKey;

    @Value("${COOLSMS_API_SECRET}")
    private String apiSecret;

    @Value("${COOLSMS_SENDER_NUMBER}")
    private String senderNumber;

    private DefaultMessageService messageService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SMS_CODE_PREFIX = "SMS:";
    private static final long CODE_EXPIRATION_TIME = 300000; // 5분

    public SmsController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void initialize() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    /**
     * SMS 인증번호 발송
     */
    @PostMapping("/sms/verification")
    public ResponseEntity<JSONResponse<SingleMessageSentResponse>> sendSmsVerification(@RequestParam String phoneNumber) {
        try {
        // 인증번호 생성 (6자리)
        String verificationCode = generateRandomCode();

        // 로그 추가
        log.info("Sending SMS to: {}", phoneNumber);
        log.info("Verification code: {}", verificationCode);
        log.info("Sender number: {}", senderNumber);

        // Redis에 인증번호 저장
        String redisKey = SMS_CODE_PREFIX + phoneNumber;
        redisTemplate.opsForValue().set(redisKey, verificationCode, CODE_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        // 메시지 생성
        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(phoneNumber);
        message.setText(String.format("[PillMe] [인증번호: %s] 인증번호는 5분간 유효합니다.", verificationCode));

        // 메시지 발송
        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));

        // 응답 로그 추가
        log.info("SMS Response: {}", response);

        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    } catch (Exception e) {
        log.error("SMS sending failed", e);
        throw e;
    }
    }

    /**
     * 6자리 랜덤 인증번호 생성
     */
    private String generateRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}

