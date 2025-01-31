package com.ssafy.pillme.notification.presentation.controller;

import com.ssafy.pillme.global.code.SuccessCode;
import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.notification.application.response.NotificationSettingResponse;
import com.ssafy.pillme.notification.application.service.NotificationService;
import com.ssafy.pillme.notification.presentation.request.NotificationSettingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification/setting")
@RequiredArgsConstructor
public class NotificationSettingController {

    private final NotificationService notificationService;

    // 알림 설정 생성
    //TODO: 회원 데이터 추가 필요
    @PostMapping
    public ResponseEntity<JSONResponse<Void>> createNotificationSetting(@RequestBody NotificationSettingRequest request) {
        notificationService.createNotificationSetting(request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 알림 설정 조회
    @GetMapping
    public ResponseEntity<JSONResponse<NotificationSettingResponse>> getNotificationSetting() {
        return ResponseEntity.ok(JSONResponse.onSuccess(notificationService.getNotificationSetting()));
    }

    // 알림 설정 수정
    @PutMapping
    public JSONResponse<Void> updateNotificationSetting(@RequestBody NotificationSettingRequest request) {
        notificationService.updateNotificationSetting(request);
        return JSONResponse.of(SuccessCode.REQUEST_SUCCESS);
    }

    // 알림 설정 삭제
    @DeleteMapping
    public JSONResponse<Void> deleteNotificationSetting() {
        notificationService.deleteNotificationSetting();
        return JSONResponse.of(SuccessCode.REQUEST_SUCCESS);
    }
}
