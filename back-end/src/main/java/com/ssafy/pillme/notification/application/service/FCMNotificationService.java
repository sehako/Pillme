package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.notification.presentation.request.NotificationRequest;

public interface FCMNotificationService {
    // 사용자가 설정한 알림 설정에 따라 알림을 보내는 메서드
    void sendNotificationSetting(Long memberId, String title, String body);
    // 사용자의 요청(관계 등록/약 등록)에 따라 알림을 보내는 메서드
    void sendNotification(NotificationRequest notificationRequest);
}
