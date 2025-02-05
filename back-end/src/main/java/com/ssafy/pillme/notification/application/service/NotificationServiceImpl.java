package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.infrastructure.repository.MemberRepository;
import com.ssafy.pillme.notification.application.response.NotificationResponse;
import com.ssafy.pillme.notification.application.response.NotificationSettingResponse;
import com.ssafy.pillme.notification.domain.entity.Notification;
import com.ssafy.pillme.notification.domain.entity.NotificationSetting;
import com.ssafy.pillme.notification.domain.vo.NotificationTimeType;
import com.ssafy.pillme.notification.infrastructure.repository.NotificationRepository;
import com.ssafy.pillme.notification.infrastructure.repository.NotificationSettingRepository;
import com.ssafy.pillme.notification.presentation.request.NotificationConfirmRequest;
import com.ssafy.pillme.notification.presentation.request.NotificationDeleteRequest;
import com.ssafy.pillme.notification.presentation.request.NotificationRequest;
import com.ssafy.pillme.notification.presentation.request.NotificationSettingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor // 생성자 주입
public class NotificationServiceImpl implements NotificationService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final MemberRepository memberRepository;
    private final FCMNotificationService fcmNotificationService;
    private final NotificationRepository notificationRepository;

    //TODO: 회원 데이터 추가 필요
    @Override
    public void createNotificationSetting(NotificationSettingRequest request) {
        // request.toEntity(new UserEntity());
        notificationSettingRepository.save(request.toEntity(memberRepository.findById(1L).get()));
    }

    @Override
    public NotificationSettingResponse getNotificationSetting() {
        //TODO: 회원 데이터 추가 필요
        //TODO: 데이터 존재하지 않을 시, 예외 처리 필요
        NotificationSetting setting = notificationSettingRepository.findByMemberId(1L)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 존재하지 않습니다."));

        return NotificationSettingResponse.builder()
                .id(setting.getId())
                .morning(setting.getMorning())
                .lunch(setting.getLunch())
                .dinner(setting.getDinner())
                .sleep(setting.getSleep())
                .build();
    }

    @Override
    public void updateNotificationSetting(NotificationSettingRequest request) {
        //TODO: 회원 데이터 추가 필요
        NotificationSetting setting = notificationSettingRepository.findByMemberId(1L)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 존재하지 않습니다."));

        setting.update(request);
    }

    @Override
    public void deleteNotificationSetting() {
        // TODO: 회원 데이터 추가 필요
        NotificationSetting setting = notificationSettingRepository.findByMemberId(1L)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 존재하지 않습니다."));
        notificationSettingRepository.delete(setting);
    }

    @Override
    // Cron 표현식 사용 (매분 0초에 실행)
    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendNotifications() {

        // 현재 시간 분 단위로 이용
        LocalTime currentTIme = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        // 현재 시간에 해당하는 알림 설정 조회
        List<NotificationSetting> settings = notificationSettingRepository.findSettingsForCurrentTime(currentTIme);

        // 알림 전송
        for (NotificationSetting setting : settings) {
            sendNotification(setting, currentTIme);
        }
    }

    /*
    * 관계 등록 요청 시, 해당 알림을 DB에 저장 후,
    * 알림을 수신자에게 전송
    * */
    @Override
    public void sendDependencyRequestNotification(Member sender, Member receiver) {
        // 관계 요청 알림 생성
        Notification notification = Notification.createDependencyRequest(sender, receiver);

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

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));
    }

    /*
    * 관계 등록 요청 거절 시, 해당 알림을 DB에 저장 후,
    * 알림을 수신자에게 전송
    * */
    @Override
    public void sendDependencyRejectNotification(Member sender, Member receiver) {
        // 관계 요청 거절 알림 생성
        Notification notification = Notification.createDependencyReject(sender, receiver);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));
    }

    /*
    * 약 등록 요청 시, 해당 알림을 DB에 저장 후,
    * 알림을 수신자에게 전송
    * */
    @Override
    public void sendMedicineRequestNotification(Member sender, Member receiver) {
        // 약 등록 요청 알림 생성
        Notification notification = Notification.createMedicineRequest(sender, receiver);

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

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));
    }

    /*
     * 약 등록 요청 거절 시, 해당 알림을 DB에 저장 후,
     * 알림을 수신자에게 전송
     * */
    @Override
    public void sendMedicineRejectNotification(Member sender, Member receiver) {
        // 약 등록 요청 거절 알림 생성
        Notification notification = Notification.createMedicineReject(sender, receiver);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));
    }

    /*
    * 관계 삭제 요청 시, 해당 알림을 DB에 저장 후,
    * 알림을 수신자에게 전송
    * */
    @Override
    public void sendDependencyDeleteRequestNotification(Member sender, Member receiver) {
        // 관계 삭제 요청 알림 생성
        Notification notification = Notification.createDependencyDeleteRequest(sender, receiver);

        // 알림 저장
        notificationRepository.save(notification);

        // 알림 전송
        fcmNotificationService.sendNotification(NotificationRequest.of(notification));
    }

    // 알림 리스트 조회
    @Override
    public List<NotificationResponse> getNotificationList() {
        // TODO: 회원 데이터 추가 필요
        Member member = memberRepository.findById(1L).get();

        List<Notification> notifications = notificationRepository.findAllByReceiverIdAndDeletedFalse(member.getId());

        return NotificationResponse.listOf(notifications);
    }

    /*
    * 여러 개의 알림 혹은 한 개의 알림을 읽은 것으로 처리
    * */
    @Override
    public void readNotifications(NotificationConfirmRequest request) {
        // TODO: 회원 데이터 추가 필요
        Member member = memberRepository.findById(1L).get();

        // 현재 사용자 id와 요청으로 받은 알림 id를 통해 알림 조회
        List<Notification> notifications = notificationRepository
                .findAllByIdInAndReceiverId(request.notificationConfirmList(), member.getId());

        // 요청한 알림과 실제 조회된 알림의 개수가 다를 경우 예외 처리
        // TODO: 예외 정의 필요
        if (notifications.size() != request.notificationConfirmList().size()) {
            throw new IllegalArgumentException("일부 알림에 대한 접근 권한이 없습니다");
        }

        // 읽은 알림 처리
        for (Notification notification : notifications) {
            notification.updateConfirmStatus(true);
        }
    }

    @Override
    public void deleteNotifications(NotificationDeleteRequest request) {
        // TODO: 회원 데이터 추가 필요
        Member member = memberRepository.findById(1L).get();

        // 현재 사용자 id와 요청으로 받은 알림 id를 통해 알림 조회
        List<Notification> notifications = notificationRepository
                .findAllByIdInAndReceiverId(request.notificationDeleteList(), member.getId());

        // 요청한 알림과 실제 조회된 알림의 개수가 다를 경우 예외 처리
        // TODO: 예외 정의 필요
        if (notifications.size() != request.notificationDeleteList().size()) {
            throw new IllegalArgumentException("일부 알림에 대한 접근 권한이 없습니다");
        }

        // 알림 soft 삭제
        for (Notification notification : notifications) {
            notification.updateDeleteStatus();
        }
    }

    // 일관된 시간을 위해 currentTime을 매개변수로 받아 이용
    private void sendNotification(NotificationSetting setting, LocalTime currentTime) {
        try {
            // 알림 시간 타입 확인
            NotificationTimeType timeType = NotificationTimeType.determineType(setting, currentTime);

            //TODO: 회원의 약물 복용이 존재하는 경우, 해당 약물의 이름과 함께 알림 전송
            // 현재는 알림 설정만 존재하므로 해당 시간에 알림 제목만 전송
            fcmNotificationService.sendNotificationSetting(1L, timeType.getMessage(), "");
        } catch (Exception e) {
            log.error("알림 전송 중 오류 발생: {}", e.getMessage());
        }
    }
}
