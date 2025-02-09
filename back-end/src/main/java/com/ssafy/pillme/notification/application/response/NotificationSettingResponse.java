package com.ssafy.pillme.notification.application.response;

import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class NotificationSettingResponse {
    private Integer notificationSettingId;
    private LocalTime morning;
    private LocalTime lunch;
    private LocalTime dinner;
    private LocalTime sleep;
}

