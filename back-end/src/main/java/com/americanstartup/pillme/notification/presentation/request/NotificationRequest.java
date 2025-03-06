package com.americanstartup.pillme.notification.presentation.request;

import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.notification.domain.entity.Notification;
import com.americanstartup.pillme.notification.domain.vo.NotificationCode;
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
