package com.ssafy.pillme.notification.presentation.request;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.notification.domain.vo.NotificationCode;
import lombok.Builder;

@Builder
public record ChatNotificationRequest(
        Long chatRoomId,
        Member sender,
        Member receiver,
        String message,
        Long sendTime,
        NotificationCode notificationCode
) {
    public static ChatNotificationRequest of(Long chatRoomId, Member sender, Member receiver, String message, Long sendTime, NotificationCode notificationCode) {
        return ChatNotificationRequest.builder()
                .chatRoomId(chatRoomId)
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .sendTime(sendTime)
                .notificationCode(notificationCode)
                .build();
    }
}
