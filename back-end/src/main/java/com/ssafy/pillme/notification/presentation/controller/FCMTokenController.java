package com.ssafy.pillme.notification.presentation.controller;

import com.ssafy.pillme.global.code.SuccessCode;
import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.notification.application.service.FCMTokenService;
import com.ssafy.pillme.notification.presentation.request.FCMTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
public class FCMTokenController {

    private final FCMTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<JSONResponse<Void>> createToken(@RequestBody FCMTokenRequest request) {
        fcmTokenService.createToken(request);

        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
