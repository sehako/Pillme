package com.ssafy.pillme.notification.presentation.request;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.notification.domain.entity.Notification;
import com.ssafy.pillme.notification.domain.vo.NotificationCode;
import lombok.Builder;

@Builder
public record NotificationRequest(Member sender, Member receiver, NotificationCode code, String content) {

    public static NotificationRequest of(Notification notification) {
        return NotificationRequest.builder()
                .sender(notification.getSender())
                .receiver(notification.getReceiver())
                .code(notification.getCode())
                .content(notification.getContent())
                .build();
    }
}
