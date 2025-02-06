package com.ssafy.pillme.auth.presentation.controller;

import com.ssafy.pillme.auth.application.exception.sms.*;
import com.ssafy.pillme.auth.application.service.SmsService;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.*;
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
@RequiredArgsConstructor
public class SmsController {
    private final SmsService smsService;

    @PostMapping("/sms/verification")
    public ResponseEntity<JSONResponse<SingleMessageSentResponse>> sendSmsVerification(@RequestParam String phoneNumber) {
        SingleMessageSentResponse response = smsService.sendVerificationSms(phoneNumber);
        return ResponseEntity.ok(JSONResponse.onSuccess(response));
    }

    @PostMapping("/sms/verify")
    public ResponseEntity<JSONResponse<Void>> verifySmsCode(
            @RequestParam String phoneNumber,
            @RequestParam String code) {
        smsService.verifySmsCode(phoneNumber, code);
        return ResponseEntity.ok(JSONResponse.onSuccess(null));
    }
}

