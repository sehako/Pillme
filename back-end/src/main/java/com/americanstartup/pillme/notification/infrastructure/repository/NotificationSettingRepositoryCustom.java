package com.americanstartup.pillme.notification.infrastructure.repository;

import com.americanstartup.pillme.notification.domain.entity.NotificationSetting;
import java.time.LocalTime;
import java.util.List;

public interface NotificationSettingRepositoryCustom {
    List<NotificationSetting> findSettingsForCurrentTime(LocalTime currentTime);
}
