package com.americanstartup.pillme.notification.application.response;

import com.americanstartup.pillme.notification.domain.entity.Notification;
import com.americanstartup.pillme.notification.domain.vo.NotificationCode;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
public class NotificationResponse {
    private Long notificationId;
    private Long senderId;
    private NotificationCode code;
    private String content;
    private Timestamp createdAt;
    private boolean confirm;

    public static List<NotificationResponse> listOf(List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationResponse::of)
                .toList();
    }

    public static NotificationResponse of(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .senderId(notification.getSender().getId())
                .code(notification.getCode())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .confirm(notification.isConfirm())
                .build();
    }
}
