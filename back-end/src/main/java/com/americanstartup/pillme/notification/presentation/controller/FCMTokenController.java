package com.americanstartup.pillme.notification.presentation.controller;

import com.americanstartup.pillme.auth.annotation.Auth;
import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.global.response.JSONResponse;
import com.americanstartup.pillme.notification.application.service.FCMTokenService;
import com.americanstartup.pillme.notification.presentation.request.FCMTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
public class FCMTokenController {

    private final FCMTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<JSONResponse<Void>> createToken(@RequestBody FCMTokenRequest request, @Auth Member loginMember) {
        fcmTokenService.createToken(request, loginMember);

        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    @DeleteMapping("/{fcmToken}")
    public ResponseEntity<JSONResponse<Void>> deleteToken(@PathVariable("fcmToken") String fcmToken, @Auth Member loginMember) {
        fcmTokenService.deleteFCMToken(fcmToken, loginMember);

        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
