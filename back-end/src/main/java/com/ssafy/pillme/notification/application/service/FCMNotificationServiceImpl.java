package com.ssafy.pillme.notification.application.service;

import com.google.firebase.messaging.*;
import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.notification.application.exception.FCMTokenNotFoundException;
import com.ssafy.pillme.notification.domain.entity.FCMToken;
import com.ssafy.pillme.notification.presentation.request.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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

        for (FCMToken token : tokens) {
            Message message = buildSendNotificationMessage(token.getToken(), title, body);

            /* 사용자가 알림을 허용한 토큰이 여러 개일 수 있기 때문에 비동기로 처리
            동기로 처리할 경우, 모든 토큰에 알림을 보내는데 시간이 오래 걸릴 수 있음
             */
            try {
                firebaseMessaging.sendAsync(message);
            } catch (Exception e) {
                handleExceptionForSendMessage(e, token);
            }
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
        List<FCMToken> receiverFCMTokens = findValidTokens(notificationRequest.receiver().getId());

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
                                    .putCustomData(DataKey.CODE, data.get(DataKey.CODE))
                                    .setTitle(data.get(DataKey.TITLE))
                                    .setBody(data.get(DataKey.BODY))
                                    // 발신자 정보 추가 (알림 처리 시 필요)
                                    .putCustomData(DataKey.SENDER_ID, data.get(DataKey.SENDER_ID))
                                    .build())
                            .build())
                    .build();
            try {
                firebaseMessaging.sendAsync(message);
            } catch (Exception e) {
                handleExceptionForSendMessage(e, receiverFCMToken);
            }

        }
    }

    // 메시지 전송 중 예외 처리
    private void handleExceptionForSendMessage(Exception e, FCMToken fcmToken) {

        if (e.getCause() instanceof FirebaseMessagingException exception) {
            MessagingErrorCode errorCode = exception.getMessagingErrorCode();
            // 알림 전송 실패 시 처리
            if (errorCode.equals(MessagingErrorCode.UNREGISTERED)) {
                fcmTokenService.deleteFCMToken(fcmToken);
            }
            /*
             * FCM 서버에서 장애가 발생했을 때, 재전송 로직을 추가
             * */
            else if (errorCode.equals(MessagingErrorCode.UNAVAILABLE) ||
                    errorCode.equals(MessagingErrorCode.INTERNAL)) {
                // TODO: 재전송 로직 추가
            }
            // 기타 예외는 로그로 기록
            else {
                log.error("Failed to send message: {}", e.getMessage());
            }
        }
        // 기타 예외는 로그로 기록
        else {
            log.error("Failed to send message: {}", e.getMessage());
        }
    }

    // 알림 데이터 설정
    private Map<String, String> setNotificationData(NotificationRequest notificationRequest) {
        Map<String, String> data = new HashMap<>();
        // 어떤 알림인지 구분하기 위한 코드
        data.put(DataKey.CODE, notificationRequest.code().getCode());
        // 알림 제목
        data.put(DataKey.TITLE, notificationRequest.code().getTitle());
        // 알림 내용
        data.put(DataKey.BODY, notificationRequest.content());
        // 알림 발신자 id
        data.put(DataKey.SENDER_ID, notificationRequest.sender().getId().toString());

        return data;
    }

    // FCMTokenService를 통해 사용자의 토큰들을 조회하고 검증
    private List<FCMToken> findValidTokens(Long memberId) {
        List<FCMToken> tokens = fcmTokenService.findAllByMemberId(memberId);

        // 사용자가 알림을 허용한 토큰이 없을 경우 예외 처리
        if (tokens.isEmpty()) {
            throw new FCMTokenNotFoundException(ErrorCode.FCM_TOKEN_NOT_FOUND);
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
