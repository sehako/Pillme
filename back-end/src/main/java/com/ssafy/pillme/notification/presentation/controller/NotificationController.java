package com.ssafy.pillme.notification.presentation.controller;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.notification.application.response.NotificationResponse;
import com.ssafy.pillme.notification.application.service.NotificationService;
import com.ssafy.pillme.notification.presentation.request.NotificationConfirmRequest;
import com.ssafy.pillme.notification.presentation.request.NotificationDeleteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 알림 메시지 리스트
    @GetMapping
    public ResponseEntity<JSONResponse<List<NotificationResponse>>> getNotificationList() {
        return ResponseEntity.ok(JSONResponse.onSuccess(notificationService.getNotificationList()));
    }

    // 알림 읽음 기능
    @PutMapping
    public ResponseEntity<JSONResponse<Void>> readNotifications(@RequestBody NotificationConfirmRequest request) {
        notificationService.readNotifications(request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 알림 삭제 기능
    @DeleteMapping
    public ResponseEntity<JSONResponse<Void>> deleteNotificationSetting(@RequestBody NotificationDeleteRequest request) {
        notificationService.deleteNotifications(request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
