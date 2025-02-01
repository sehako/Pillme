package com.ssafy.pillme.notification.application.response;

import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSettingResponse {
    private Integer id;
    private LocalTime morning;
    private LocalTime lunch;
    private LocalTime dinner;
    private LocalTime sleep;
}

