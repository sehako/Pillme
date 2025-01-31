package com.ssafy.pillme.notification.application.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class NotificationSettingResponse {
    private Integer id;
    private LocalTime morning;
    private LocalTime lunch;
    private LocalTime dinner;
    private LocalTime sleep;
}
