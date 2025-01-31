package com.ssafy.pillme.notification.application.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.pillme.notification.domain.entity.FCMToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FCMNotificationServiceImpl implements FCMNotificationService {

    private final FCMTokenService fcmTokenService;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendNotificationSetting(Long memberId, String title, String body) {

        // 사용자 id로 토큰들 조회
        // 사용자가 알림을 허용한 모든 토큰들에 알림 전송
        fcmTokenService.findByMemberId(memberId).ifPresent(tokens -> {
            tokens.forEach(token -> {
                Message message = Message.builder()
                        .setToken(token.getToken())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build();

                // 사용자가 알림을 허용한 토큰이 여러 개일 수 있기 때문에 비동기로 처리
                // 동기로 처리할 경우, 모든 토큰에 알림을 보내는데 시간이 오래 걸릴 수 있음
                firebaseMessaging.sendAsync(message);
            });
        });
    }
}