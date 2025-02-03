package com.ssafy.pillme.notification.application.service;

import com.google.firebase.messaging.*;
import com.ssafy.pillme.notification.domain.entity.FCMToken;
import com.ssafy.pillme.notification.presentation.request.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FCMNotificationServiceImpl implements FCMNotificationService {

    private final FCMTokenService fcmTokenService;
    private final FirebaseMessaging firebaseMessaging;

    /*
     * FCM에서 알림을 보내기 위한 데이터 키 정의
     * code: 알림의 종류를 구분하기 위한 키
     * title: 알림의 제목
     * body: 알림의 내용
     * senderId: 알림을 보내는 사용자의 id(primary key)
     * */
    private static final class DataKey {
        private static final String CODE = "code";
        private static final String TITLE = "title";
        private static final String BODY = "body";
        private static final String SENDER_ID = "senderId";
    }

    @Override
    public void sendNotificationSetting(Long memberId, String title, String body) {

        /* 사용자 id로 토큰들 조회
        사용자가 알림을 허용한 모든 토큰들에 알림 전송
         */
        List<FCMToken> tokens = findValidTokens(memberId);

        if (tokens.isEmpty()) {
            //TODO: 사용자가 알림을 허용한 토큰이 없을 경우 예외 처리
        }

        for (FCMToken token : tokens) {
            Message message = buildSendNotificationMessage(token.getToken(), title, body);

            /* 사용자가 알림을 허용한 토큰이 여러 개일 수 있기 때문에 비동기로 처리
            동기로 처리할 경우, 모든 토큰에 알림을 보내는데 시간이 오래 걸릴 수 있음
             */
            firebaseMessaging.sendAsync(message);
        }
    }

    /*
     * 웹 푸시 알림을 전송하는 메소드
     * 수신자의 모든 유효한 FCM 토큰에 대해 알림을 전송
     *
     * @param notificationRequest: 알림 전송에 필요한 정보를 담은 객체
     * */
    @Override
    public void sendNotification(NotificationRequest notificationRequest) {
        // 수신자의 id로 토큰들 조회
        List<FCMToken> receiverFCMTokens = findValidTokens(notificationRequest.receiver().extractUserInfo().id());

        /*
         * 알림에 필요한 데이터 설정
         * - 알림 코드 (CODE) - 알림 종류를 구분하기 위한 코드
         * - 알림 제목 (TITLE)
         * - 알림 내용 (BODY)
         * - 알림 발신자 id (SENDER_ID)
         * */
        Map<String, String> data = setNotificationData(notificationRequest);

        for (FCMToken receiverFCMToken : receiverFCMTokens) {
            Message message = Message.builder()
                    .setToken(receiverFCMToken.getToken())
                    .putAllData(data) // 전체 데이터를 메시지에 포함
                    // 웹 푸시 특화 설정 시작
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(WebpushNotification.builder()
                                    .putCustomData(dataKey.CODE, data.get(dataKey.CODE))
                                    .setTitle(data.get(dataKey.TITLE))
                                    .setBody(data.get(dataKey.BODY))
                                    // 발신자 정보 추가 (알림 처리 시 필요)
                                    .putCustomData(dataKey.SENDER_ID, data.get(dataKey.SENDER_ID))
                                    .build())
                            .build())
                    .build();
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                // TODO: 예외 처리 로직 추가
            }

        }
    }

    // 알림 데이터 설정
    private Map<String, String> setNotificationData(NotificationRequest notificationRequest) {
        Map<String, String> data = new HashMap<>();
        // 어떤 알림인지 구분하기 위한 코드
        data.put(dataKey.CODE, notificationRequest.code().getCode());
        // 알림 제목
        data.put(dataKey.TITLE, notificationRequest.code().getTitle());
        // 알림 내용
        data.put(dataKey.BODY, notificationRequest.content());
        // 알림 발신자 id
        data.put(dataKey.SENDER_ID, notificationRequest.sender().extractUserInfo().id().toString());

        return data;
    }

    // FCMTokenService를 통해 사용자의 토큰들을 조회하고 검증
    private List<FCMToken> findValidTokens(Long memberId) {
        List<FCMToken> tokens = fcmTokenService.findAllByMemberId(memberId);

        // 사용자가 알림을 허용한 토큰이 없을 경우 예외 처리
        if (tokens.isEmpty()) {
            // TODO: 사용자가 알림을 허용한 토큰이 없을 경우 예외 처리
        }

        return tokens;
    }

    // 복용 알림 메시지 생성
    private Message buildSendNotificationMessage(String token, String title, String body) {
        return Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
    }

}
