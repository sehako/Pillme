package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.auth.infrastructure.repository.UserRepository;
import com.ssafy.pillme.notification.application.response.NotificationSettingResponse;
import com.ssafy.pillme.notification.domain.entity.NotificationSetting;
import com.ssafy.pillme.notification.domain.vo.NotificationTimeType;
import com.ssafy.pillme.notification.infrastructure.repository.NotificationSettingRepository;
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
    private final UserRepository userRepository;
    private final FCMNotificationService fcmNotificationService;

    //TODO: 회원 데이터 추가 필요
    @Override
    public void createNotificationSetting(NotificationSettingRequest request) {
        // request.toEntity(new UserEntity());
        notificationSettingRepository.save(request.toEntity(userRepository.findById(1L).get()));
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

    // 일관된 시간을 위해 currentTIme을 매개변수로 받아 이용
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
