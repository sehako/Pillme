package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.notification.application.response.NotificationResponse;
import com.ssafy.pillme.notification.application.response.NotificationSettingResponse;
import com.ssafy.pillme.notification.presentation.request.NotificationConfirmRequest;
import com.ssafy.pillme.notification.presentation.request.NotificationDeleteRequest;
import com.ssafy.pillme.notification.presentation.request.NotificationSettingRequest;

import java.util.List;

public interface NotificationService {
    void createNotificationSetting(NotificationSettingRequest request);

    NotificationSettingResponse getNotificationSetting();

    void updateNotificationSetting(NotificationSettingRequest request);

    void deleteNotificationSetting();

    void checkAndSendNotifications();

    void sendDependencyRequestNotification(Member sender, Member receiver);

    void sendDependencyAcceptNotification(Member sender, Member receiver);

    void sendDependencyRejectNotification(Member sender, Member receiver);

    void sendMedicineRequestNotification(Member sender, Member receiver);

    void sendMedicineAcceptNotification(Member sender, Member receiver);

    void sendMedicineRejectNotification(Member sender, Member receiver);

    void sendDependencyDeleteRequestNotification(Member sender, Member receiver);

    void sendDependencyDeleteAcceptNotification(Member sender, Member receiver);

    List<NotificationResponse> getNotificationList();

    void readNotifications(NotificationConfirmRequest request);

    void deleteNotifications(NotificationDeleteRequest request);
}
