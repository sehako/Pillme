package com.ssafy.pillme.notification.application.service;

import com.google.firebase.messaging.*;
import com.ssafy.pillme.notification.domain.entity.FCMToken;
import com.ssafy.pillme.notification.presentation.request.ChatNotificationRequest;
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

        // 채팅 알림을 위한 데이터 키
        private static final String CHAT_ROOM_ID = "chatRoomId";
        private static final String SEND_TIME = "sendTime";
        private static final String RECEIVER_ID = "receiverId";
        private static final String SENDER_NAME = "senderName";
        private static final String RECEIVER_NAME = "receiverName";

        // 삭제할 관계 id
        private static final String DEPENDENCY_ID = "dependencyId";
    }

    // 서비스 홈페이지 URL
    private static final String SERVICE_URL = "https://pillme.site";

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
                    // 웹/PWA용 설정
                    .setWebpushConfig(WebpushConfig.builder()
                            // 알림 클릭 시 이동할 URL 설정 (현재는 모두 서비스 홈 URL)
                            .setFcmOptions(WebpushFcmOptions.builder()
                                    .setLink(SERVICE_URL)
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

    @Override
    public void sendDeleteDependencyNotification(NotificationRequest notificationRequest, Long dependencyId) {
        // 수신자의 id로 토큰들 조회
        List<FCMToken> receiverFCMTokens = findValidTokens(notificationRequest.receiver().getId());

        // 알림 데이터 설정
        Map<String, String> data = setNotificationData(notificationRequest);
        // 알림 데이터에 삭제할 관계 id 추가
        data.put(DataKey.DEPENDENCY_ID, dependencyId.toString());

        for (FCMToken receiverFCMToken : receiverFCMTokens) {
            Message message = Message.builder()
                    .setToken(receiverFCMToken.getToken())
                    .putAllData(data) // 전체 데이터를 메시지에 포함
                    // 웹/PWA용 설정
                    .setWebpushConfig(WebpushConfig.builder()
                            // 알림 클릭 시 이동할 URL 설정 (현재는 모두 서비스 홈 URL)
                            .setFcmOptions(WebpushFcmOptions.builder()
                                    .setLink(SERVICE_URL)
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

    @Override
    public void sendToProtectorNotificationForTaking(Long protectorId, String title, String body) {
        // 보호자 id로 토큰들 조회
        List<FCMToken> tokens = findValidTokens(protectorId);

        for (FCMToken token : tokens) {
            Message message = buildSendNotificationMessage(token.getToken(), title, body);

            try {
                firebaseMessaging.sendAsync(message);
            } catch (Exception e) {
                handleExceptionForSendMessage(e, token);
            }
        }
    }


    /*
     * 채팅 알림을 전송하는 메소드
     * ChatNotificationRequest
     * - chatRoomId: 채팅방 id
     * - senderId: 메시지를 보낸 사용자
     * - receiverId: 메시지를 받는 사용자
     * - message: 채팅 메시지 내용
     * - sendTime: 채팅 메시지 전송 시간
     * */
    @Override
    public void sendChatNotification(ChatNotificationRequest chatNotificationRequest) {
        // 수신자의 id로 토큰들 조회
        List<FCMToken> receiverFCMTokens = findValidTokens(chatNotificationRequest.receiver().getId());

        // 채팅 알림 데이터 설정
        Map<String, String> data = setChatNotificationData(chatNotificationRequest);

        for (FCMToken receiverFCMToken : receiverFCMTokens) {
            Message message = Message.builder()
                    .setToken(receiverFCMToken.getToken())
                    .putAllData(data) // 전체 데이터를 메시지에 포함
                    // 웹/PWA용 설정
                    .setWebpushConfig(WebpushConfig.builder()
                            // 알림 클릭 시 이동할 URL 설정 (현재는 모두 서비스 홈 URL)
                            .setFcmOptions(WebpushFcmOptions.builder()
                                    .setLink(SERVICE_URL)
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

    // 채팅 알림 데이터 설정
    private Map<String, String> setChatNotificationData(ChatNotificationRequest chatNotificationRequest) {
        Map<String, String> data = new HashMap<>();
        // 어떤 알림인지 구분하기 위한 코드
        data.put(DataKey.CODE, chatNotificationRequest.notificationCode().getCode());
        // 알림 제목 (보낸 사람 이름)
        data.put(DataKey.TITLE, chatNotificationRequest.sender().getName());
        // 알림 내용
        data.put(DataKey.BODY, chatNotificationRequest.message());
        // 알림 발신자 id
        data.put(DataKey.SENDER_ID, chatNotificationRequest.sender().getId().toString());
        // 채팅방 id
        data.put(DataKey.CHAT_ROOM_ID, chatNotificationRequest.chatRoomId().toString());
        // 채팅 메시지 전송 시간
        data.put(DataKey.SEND_TIME, chatNotificationRequest.sendTime().toString());
        // 채팅 메시지 수신자 id
        data.put(DataKey.RECEIVER_ID, chatNotificationRequest.receiver().getId().toString());
        // 채팅 메시지 발신자 이름
        data.put(DataKey.SENDER_NAME, chatNotificationRequest.sender().getName());
        // 채팅 메시지 수신자 이름
        data.put(DataKey.RECEIVER_NAME, chatNotificationRequest.receiver().getName());

        return data;
    }

    // FCMTokenService를 통해 사용자의 토큰들을 조회하고 검증
    private List<FCMToken> findValidTokens(Long memberId) {
        List<FCMToken> tokens = fcmTokenService.findAllByMemberId(memberId);

        // 토큰이 없을 경우, 로그로 기록만 남기고 빈 리스트 반환
        if (tokens.isEmpty()) {
            log.error("FCM Token not found for member id: {}", memberId);
            return List.of();
        }

        return tokens;
    }

    // 복용 알림 메시지 생성
    private Message buildSendNotificationMessage(String token, String title, String body) {
        return Message.builder()
                .setToken(token)
                // 기본 알림 설정
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                // 웹/PWA용 설정
                .setWebpushConfig(WebpushConfig.builder()
                        // 알림 클릭 시 이동할 URL 설정 (현재는 모두 서비스 홈 URL)
                        .setFcmOptions(WebpushFcmOptions.builder()
                                .setLink(SERVICE_URL)
                                .build())
                        .build())
                .build();
    }

}
