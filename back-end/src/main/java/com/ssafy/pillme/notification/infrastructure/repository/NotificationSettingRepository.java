package com.ssafy.pillme.notification.infrastructure.repository;

import com.ssafy.pillme.notification.domain.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Integer> {

    Optional<NotificationSetting> findByUserId(Integer userId);
}
