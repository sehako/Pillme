package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.notification.application.exception.NotificationAccessDenied;
import com.ssafy.pillme.notification.application.exception.NotificationRequestDuplicateException;
import com.ssafy.pillme.notification.application.exception.NotificationRequestNotFoundException;
import com.ssafy.pillme.notification.application.exception.NotificationSettingNotFoundException;
import com.ssafy.pillme.notification.application.response.NotificationResponse;
import com.ssafy.pillme.notification.application.response.NotificationSettingResponse;
import com.ssafy.pillme.notification.domain.entity.Notification;
import com.ssafy.pillme.notification.domain.entity.NotificationSetting;
import com.ssafy.pillme.notification.domain.vo.NotificationCode;
import com.ssafy.pillme.notification.infrastructure.repository.NotificationRepository;
import com.ssafy.pillme.notification.infrastructure.repository.NotificationSettingRepository;
import com.ssafy.pillme.notification.presentation.request.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor // 생성자 주입
public class NotificationServiceImpl implements NotificationService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final FCMNotificationService fcmNotificationService;
    private final NotificationRepository notificationRepository;

    @Override
    public void createNotificationSetting(NotificationSettingRequest request, Member loginMember) {
        notificationSettingRepository.save(request.toEntity(loginMember));
    }

    @Override
    public NotificationSettingResponse getNotificationSetting(Member loginMember) {
        NotificationSetting setting = notificationSettingRepository.findByMemberId(loginMember.getId())
                .orElse(null);

        // 알림 설정이 존재하지 않을 경우 빈 응답 반환
        if (setting == null) {
            return NotificationSettingResponse.builder().build();
        }

        return NotificationSettingResponse.builder()
                .notificationSettingId(setting.getId())
                .morning(setting.getMorning())
                .lunch(setting.getLunch())
                .dinner(setting.getDinner())
                .sleep(setting.getSleep())
                .build();
    }

    @Override
    public void updateNotificationSetting(NotificationSettingRequest request, Member loginMember) {
        NotificationSetting setting = notificationSettingRepository.findByMemberId(loginMember.getId())
                .orElseThrow(() -> new NotificationSettingNotFoundException(ErrorCode.NOTIFICATION_SETTING_NOT_FOUND));

        setting.update(request);
    }

    @Override
    public void deleteNotificationSetting(Member loginMember) {
        NotificationSetting setting = notificationSettingRepository.findByMemberId(loginMember.getId())
                .orElseThrow(() -> new NotificationSettingNotFoundException(ErrorCode.NOTIFICATION_SETTING_NOT_FOUND));
        notificationSettingRepository.delete(setting);
    }

    /*
     * 관계 등록 요청 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendDependencyRequestNotification(Member sender, Member receiver) {
        // 관계 요청 알림 생성
        Notification notification = Notification.createDependencyRequest(sender, receiver);

        // 등록 요청이 이미 존재하는 경우 예외 처리
        checkDuplicate(sender, receiver, NotificationCode.DEPENDENCY_REQUEST);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));
    }

    /*
     * 관계 등록 요청 수락 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendDependencyAcceptNotification(Member sender, Member receiver) {
        // 관계 요청 수락 알림 생성
        Notification notification = Notification.createDependencyAccept(sender, receiver);

        // 등록된 요청이 없는 경우 예외 처리
        Notification existNotification = checkRequestExist(sender, receiver, NotificationCode.DEPENDENCY_REQUEST);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));

        // 요청 알림 삭제
        existNotification.delete();
    }

    /*
     * 관계 등록 요청 거절 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendDependencyRejectNotification(Member sender, Member receiver) {
        // 관계 요청 거절 알림 생성
        Notification notification = Notification.createDependencyReject(sender, receiver);

        // 등록된 요청이 없는 경우 예외 처리
        Notification existNotification = checkRequestExist(sender, receiver, NotificationCode.DEPENDENCY_REQUEST);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));

        // 요청 알림 삭제
        existNotification.delete();
    }

    /*
     * 약 등록 요청 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendMedicineRequestNotification(Member sender, Member receiver) {
        // 약 등록 요청 알림 생성
        Notification notification = Notification.createMedicineRequest(sender, receiver);

        // 등록 요청이 이미 존재하는 경우 예외 처리
        checkDuplicate(sender, receiver, NotificationCode.MEDICINE_REQUEST);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));
    }

    /*
     * 약 등록 요청 수락 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendMedicineAcceptNotification(Member sender, Member receiver) {
        // 약 등록 요청 수락 알림 생성
        Notification notification = Notification.createMedicineAccept(sender, receiver);

        // 등록된 요청이 없는 경우 예외 처리
        Notification existNotification = checkRequestExist(sender, receiver, NotificationCode.MEDICINE_REQUEST);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));

        // 요청 알림 삭제
        existNotification.delete();
    }

    /*
     * 약 등록 요청 거절 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendMedicineRejectNotification(Member sender, Member receiver) {
        // 약 등록 요청 거절 알림 생성
        Notification notification = Notification.createMedicineReject(sender, receiver);

        // 등록된 요청이 없는 경우 예외 처리
        Notification existNotification = checkRequestExist(sender, receiver, NotificationCode.MEDICINE_REQUEST);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));

        // 요청 알림 삭제
        existNotification.delete();
    }

    /*
     * 관계 삭제 요청 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendDependencyDeleteRequestNotification(Member sender, Member receiver) {
        // 관계 삭제 요청 알림 생성
        Notification notification = Notification.createDependencyDeleteRequest(sender, receiver);

        // 삭제 요청이 이미 존재하는 경우 예외 처리
        checkDuplicate(sender, receiver, NotificationCode.DEPENDENCY_DELETE_REQUEST);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));
    }

    /*
     * 관계 삭제 허락 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendDependencyDeleteAcceptNotification(Member sender, Member receiver) {
        // 관계 삭제 허락 알림 생성
        Notification notification = Notification.createDependencyDeleteAccept(sender, receiver);

        // 삭제된 요청이 없는 경우 예외 처리
        Notification existNotification = checkRequestExist(sender, receiver, NotificationCode.DEPENDENCY_DELETE_REQUEST);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));

        // 요청 알림 삭제
        existNotification.delete();
    }

    /*
     * 관계 삭제 거절 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendDependencyDeleteRejectNotification(Member sender, Member receiver) {
        // 관계 삭제 거절 알림 생성
        Notification notification = Notification.createDependencyDeleteReject(sender, receiver);

        // 삭제된 요청이 없는 경우 예외 처리
        Notification existNotification = checkRequestExist(sender, receiver, NotificationCode.DEPENDENCY_DELETE_REQUEST);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));

        // 요청 알림 삭제
        existNotification.delete();
    }

    // 알림 리스트 조회
    @Override
    public List<NotificationResponse> getNotificationList(Member loginMember) {
        List<Notification> notifications = notificationRepository.findAllByReceiverIdAndDeletedFalse(loginMember.getId());

        return NotificationResponse.listOf(notifications);
    }

    /*
     * 여러 개의 알림 혹은 한 개의 알림을 읽은 것으로 처리
     * */
    @Override
    public void readNotifications(NotificationConfirmRequest request, Member loginMember) {
        // 현재 사용자 id와 요청으로 받은 알림 id를 통해 알림 조회
        List<Notification> notifications = notificationRepository
                .findAllByIdInAndReceiverIdAndDeletedFalse(request.notificationConfirmList(), loginMember.getId());

        // 요청한 알림과 실제 조회된 알림의 개수가 다를 경우 예외 처리
        if (notifications.size() != request.notificationConfirmList().size()) {
            throw new NotificationAccessDenied(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        // 읽은 알림 처리
        for (Notification notification : notifications) {
            notification.updateConfirmStatus(true);
        }
    }

    @Override
    public void deleteNotifications(NotificationDeleteRequest request, Member loginMember) {
        // 현재 사용자 id와 요청으로 받은 알림 id를 통해 알림 조회
        List<Notification> notifications = notificationRepository
                .findAllByIdInAndReceiverIdAndDeletedFalse(request.notificationDeleteList(), loginMember.getId());

        // 요청한 알림과 실제 조회된 알림의 개수가 다를 경우 예외 처리
        if (notifications.size() != request.notificationDeleteList().size()) {
            throw new NotificationAccessDenied(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        // 알림 soft 삭제
        for (Notification notification : notifications) {
            notification.delete();
        }
    }

    // 현재 시간에 해당하는 알림 설정 조회
    @Override
    public List<NotificationSetting> getNotificationSettingListForCurrentTime(LocalTime currentTime) {
        return notificationSettingRepository.findSettingsForCurrentTime(currentTime);
    }

    // 보호자가 피보호자에게 약 복용 알림 전송
    @Override
    public void sendProtectorToDependentNotification(Member sender, Member receiver) {
        // 알림 생성
        Notification notification = Notification.createProtectorToDependent(sender, receiver);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));
    }

    @Override
    public void sendChatNotification(Long chatRoomId, Member sender, Member receiver, String message, Long sendTime) {
        // 채팅 알림 요청 생성
        ChatNotificationRequest request = ChatNotificationRequest.of(chatRoomId, sender, receiver, message, sendTime, NotificationCode.CHAT_MESSAGE);

        // 알림 전송
        fcmNotificationService.sendChatNotification(request);
    }


    // 등록 요청이 이미 존재하는 경우 예외 처리
    private void checkDuplicate(Member sender, Member receiver, NotificationCode code) {
        notificationRepository.findBySenderIdAndReceiverIdAndCodeAndDeletedFalse(sender.getId(), receiver.getId(), code)
                .ifPresent(existNotification -> {
                    throw new NotificationRequestDuplicateException(ErrorCode.NOTIFICATION_REQUEST_DUPLICATE);
                });
    }

    /*
     * 등록된 요청이 없는 경우 예외 처리
     * 허락/거절 요청에서 등록 요청을 찾을 때는 받는 사람이 sender, 보낸 사람이 receiver로 전달되어야 함
     * */
    private Notification checkRequestExist(Member requestReceiver, Member requestSender, NotificationCode code) {
        return notificationRepository.findBySenderIdAndReceiverIdAndCodeAndDeletedFalse(requestSender.getId(), requestReceiver.getId(), code)
                .orElseThrow(() -> new NotificationRequestNotFoundException(ErrorCode.NOTIFICATION_REQUEST_NOT_FOUND));
    }
}
