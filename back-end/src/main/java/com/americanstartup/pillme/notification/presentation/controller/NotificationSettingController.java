package com.americanstartup.pillme.notification.presentation.controller;

import com.americanstartup.pillme.auth.annotation.Auth;
import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.global.response.JSONResponse;
import com.americanstartup.pillme.notification.application.response.NotificationSettingResponse;
import com.americanstartup.pillme.notification.application.service.NotificationService;
import com.americanstartup.pillme.notification.presentation.request.NotificationSettingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification/setting")
@RequiredArgsConstructor
public class NotificationSettingController {

    private final NotificationService notificationService;

    // 알림 설정 생성
    @PostMapping
    public ResponseEntity<JSONResponse<Void>> createNotificationSetting(@RequestBody NotificationSettingRequest request, @Auth Member loginMember) {
        notificationService.createNotificationSetting(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 알림 설정 조회
    @GetMapping
    public ResponseEntity<JSONResponse<NotificationSettingResponse>> getNotificationSetting(@Auth Member loginMember) {
        return ResponseEntity.ok(JSONResponse.onSuccess(notificationService.getNotificationSetting(loginMember)));
    }

    // 알림 설정 수정
    @PutMapping
    public ResponseEntity<JSONResponse<Void>> updateNotificationSetting(@RequestBody NotificationSettingRequest request, @Auth Member loginMember) {
        notificationService.updateNotificationSetting(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 알림 설정 삭제
    @DeleteMapping
    public ResponseEntity<JSONResponse<Void>> deleteNotificationSetting(@Auth Member loginMember) {
        notificationService.deleteNotificationSetting(loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
