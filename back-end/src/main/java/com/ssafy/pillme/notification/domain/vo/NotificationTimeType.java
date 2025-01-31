package com.ssafy.pillme.notification.domain.vo;

import com.ssafy.pillme.notification.domain.entity.NotificationSetting;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public enum NotificationTimeType {
    MORNING("아침", "아침 약을 먹을 시간입니다."),
    LUNCH("점심", "점심 약을 먹을 시간입니다."),
    DINNER("저녁", "저녁 약을 먹을 시간입니다."),
    SLEEP("잠자기 전", "잠자기 전 약을 먹을 시간입니다."),
    DEFAULT("기본", "약을 먹을 시간입니다.");

    private final String timeName;
    private final String message;

    // 사용자가 설정한 시간대에 따른 알림 시간 반환
    // 아침/점심/저녁/잠자기 전 중 설정하지 않은 값이 존재할 수 있기 때문에 null 체크
    // 설정한 시간대와 현재 시간이 같은 경우, 해당 시간대로 알림을 보내기 위해 사용
    public static NotificationTimeType determineType(NotificationSetting setting, LocalTime time) {
        if (setting.getMorning() != null && setting.getMorning().equals(time)) {
            return MORNING;
        } else if (setting.getLunch() != null && setting.getLunch().equals(time)) {
            return LUNCH;
        } else if (setting.getDinner() != null && setting.getDinner().equals(time)) {
            return DINNER;
        } else if (setting.getSleep() != null && setting.getSleep().equals(time)) {
            return SLEEP;
        }
        return DEFAULT;
    }
}
