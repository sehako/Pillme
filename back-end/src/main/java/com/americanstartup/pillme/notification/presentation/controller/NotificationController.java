package com.americanstartup.pillme.notification.presentation.controller;

import com.americanstartup.pillme.auth.annotation.Auth;
import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.global.response.JSONResponse;
import com.americanstartup.pillme.notification.application.response.NotificationResponse;
import com.americanstartup.pillme.notification.application.service.NotificationService;
import com.americanstartup.pillme.notification.presentation.request.NotificationConfirmRequest;
import com.americanstartup.pillme.notification.presentation.request.NotificationDeleteRequest;
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
    public ResponseEntity<JSONResponse<List<NotificationResponse>>> getNotificationList(@Auth Member loginMember) {
        return ResponseEntity.ok(JSONResponse.onSuccess(notificationService.getNotificationList(loginMember)));
    }

    // 알림 읽음 기능
    @PutMapping
    public ResponseEntity<JSONResponse<Void>> readNotifications(@RequestBody NotificationConfirmRequest request, @Auth Member loginMember) {
        notificationService.readNotifications(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 알림 삭제 기능
    @DeleteMapping
    public ResponseEntity<JSONResponse<Void>> deleteNotificationSetting(@RequestBody NotificationDeleteRequest request, @Auth Member loginMember) {
        notificationService.deleteNotifications(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
