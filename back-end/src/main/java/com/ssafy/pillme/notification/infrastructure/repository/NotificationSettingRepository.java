package com.ssafy.pillme.notification.infrastructure.repository;

import com.ssafy.pillme.notification.domain.entity.NotificationSetting;
import com.sun.nio.sctp.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Integer>, NotificationSettingRepositoryCustom {

    Optional<NotificationSetting> findByMemberId(Long member_id);
}
