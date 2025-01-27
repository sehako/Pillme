package com.ssafy.pillme.notification.application.service;

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
}
