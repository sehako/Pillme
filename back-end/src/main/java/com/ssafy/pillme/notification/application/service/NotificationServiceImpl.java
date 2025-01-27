package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.notification.application.response.NotificationSettingResponse;
import com.ssafy.pillme.notification.domain.entity.NotificationSetting;
import com.ssafy.pillme.notification.infrastructure.repository.NotificationSettingRepository;
import com.ssafy.pillme.notification.presentation.request.NotificationSettingRequest;
import com.ssafy.pillme.user.domain.entity.User;
import com.ssafy.pillme.user.presentation.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor // 생성자 주입
public class NotificationServiceImpl implements NotificationService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    //TODO: 회원 데이터 추가 필요
    @Override
    public void createNotificationSetting(NotificationSettingRequest request) {
        // request.toEntity(new UserEntity());
        notificationSettingRepository.save(request.toEntity(userRepository.findById(1).get()));
    }

    @Override
    public NotificationSettingResponse getNotificationSetting() {
        //TODO: 회원 데이터 추가 필요
        //TODO: 데이터 존재하지 않을 시, 예외 처리 필요
        NotificationSetting setting = notificationSettingRepository.findByUserId(1)
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
        NotificationSetting setting = notificationSettingRepository.findByUserId(1)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 존재하지 않습니다."));

        setting.update(request);
    }
}
