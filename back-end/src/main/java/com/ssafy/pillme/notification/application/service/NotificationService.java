package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.notification.presentation.request.NotificationSettingRequest;

public interface NotificationService {
    void createNotificationSetting(NotificationSettingRequest request);
}
