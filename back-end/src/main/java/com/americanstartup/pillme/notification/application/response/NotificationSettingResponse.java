package com.americanstartup.pillme.notification.application.response;

import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSettingResponse {
    private Long notificationSettingId;
    private LocalTime morning;
    private LocalTime lunch;
    private LocalTime dinner;
    private LocalTime sleep;
}

