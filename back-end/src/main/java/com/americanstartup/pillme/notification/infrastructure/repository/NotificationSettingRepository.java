package com.americanstartup.pillme.notification.infrastructure.repository;

import com.americanstartup.pillme.notification.domain.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Integer>, NotificationSettingRepositoryCustom {

    Optional<NotificationSetting> findByMemberId(Long memberId);
}
