package com.americanstartup.pillme.notification.domain.component;

import com.americanstartup.pillme.notification.domain.vo.NotificationTimeType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationMessageProvider {
    private static final Map<NotificationTimeType, String> MEDICATION_MESSAGES = Map.of(
            NotificationTimeType.MORNING, "아침 약을 먹을 시간입니다.",
            NotificationTimeType.LUNCH, "점심 약을 먹을 시간입니다.",
            NotificationTimeType.DINNER, "저녁 약을 먹을 시간입니다.",
            NotificationTimeType.SLEEP, "잠자기 전 약을 먹을 시간입니다.",
            NotificationTimeType.DEFAULT, "약을 먹을 시간입니다."
    );

    private static final Map<NotificationTimeType, String> TAKEN_MESSAGES = Map.of(
            NotificationTimeType.MORNING, "아침 약을 복용했습니다.",
            NotificationTimeType.LUNCH, "점심 약을 복용했습니다.",
            NotificationTimeType.DINNER, "저녁 약을 복용했습니다.",
            NotificationTimeType.SLEEP, "잠자기 전 약을 복용했습니다.",
            NotificationTimeType.DEFAULT, "약을 복용했습니다."
    );

    private static final Map<NotificationTimeType, String> UNTAKEN_MESSAGES = Map.of(
            NotificationTimeType.MORNING, "아침 약을 복용하지 않았습니다.",
            NotificationTimeType.LUNCH, "점심 약을 복용하지 않았습니다.",
            NotificationTimeType.DINNER, "저녁 약을 복용하지 않았습니다.",
            NotificationTimeType.SLEEP, "잠자기 전 약을 복용하지 않았습니다.",
            NotificationTimeType.DEFAULT, "약을 복용하지 않았습니다."
    );

    public String getMedicationMessage(NotificationTimeType timeType) {
        return MEDICATION_MESSAGES.get(timeType);
    }

    public String getTakenMessage(NotificationTimeType timeType) {
        return TAKEN_MESSAGES.get(timeType);
    }

    public String getNotTakenMessage(NotificationTimeType timeType) {
        return UNTAKEN_MESSAGES.get(timeType);
    }
}
