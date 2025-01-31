package com.ssafy.pillme.notification.presentation.request;


import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.notification.domain.entity.NotificationSetting;

import java.time.LocalTime;

public record NotificationSettingRequest(String morning, String lunch, String dinner, String sleep) {

    // Entity 변환
    // 설정이 없는 경우, null 값이 들어올 수 있으므로 null 체크
    public NotificationSetting toEntity(Member member) {
        return NotificationSetting.builder()
                .member(member)
                .morning(morning == null ? null : LocalTime.parse(morning))
                .lunch(lunch == null ? null : LocalTime.parse(lunch))
                .dinner(dinner == null ? null : LocalTime.parse(dinner))
                .sleep(sleep == null ? null : LocalTime.parse(sleep))
                .build();
    }
}
