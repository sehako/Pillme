package com.ssafy.pillme.notification.application.service;

public interface FCMNotificationService {
    // 사용자가 설정한 알림 설정에 따라 알림을 보내는 메서드
    void sendNotificationSetting(Integer userId, String title, String body);
}
