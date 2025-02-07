package com.ssafy.pillme.notification.application.scheduler;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.dependency.application.service.DependencyService;
import com.ssafy.pillme.management.application.ManagementService;
import com.ssafy.pillme.management.domain.item.TakingInformationItem;
import com.ssafy.pillme.notification.application.service.FCMNotificationService;
import com.ssafy.pillme.notification.application.service.NotificationService;
import com.ssafy.pillme.notification.domain.component.NotificationMessageProvider;
import com.ssafy.pillme.notification.domain.entity.NotificationSetting;
import com.ssafy.pillme.notification.domain.vo.NotificationTimeType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;
    private final NotificationMessageProvider notificationMessageProvider;
    private final DependencyService dependencyService;
    private final ManagementService managementService;
    private final FCMNotificationService fcmNotificationService;

    private static final long MEDICATION_STATUS_NOTIFICATION_DELAY_MINUTES = 5;

    // Cron 표현식 사용 (매분 0초에 실행)
    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendNotifications() {

        // 현재 시간 분 단위로 이용
        LocalTime currentTIme = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        // 현재 시간에 해당하는 알림 설정 조회
        List<NotificationSetting> settings = notificationService.getNotificationSettingListForCurrentTime(currentTIme);

        // 알림 전송
        for (NotificationSetting setting : settings) {
            sendNotification(setting, currentTIme);
        }
    }

    /*
     * 피보호자가 약 복용 여부를 5분 후에 확인하고 보호자에게 알림을 전송
     * 1. 복용 알림 시간 + 5분을 가진 회원 조회
     * 2. 해당 회원과 관계가 등록되어 있는 보호자 조회
     * 3. 해당 회원의 복용 여부 조회
     * 4. 복용 여부에 따라 보호자에게 알림 전송
     * */
    @Scheduled(cron = "0 * * * * *")
    public void sendNotificationToProtectorAboutMedicationStatus() {
        // 현재 시간 분 단위로 이용
        LocalTime currentTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        // 1. 현재 시간 - 5분에 해당하는 복용 알림 설정 조회(복용 알림 시간 + 5분)
        List<NotificationSetting> settings = notificationService.getNotificationSettingListForCurrentTime(
                currentTime.minusMinutes(MEDICATION_STATUS_NOTIFICATION_DELAY_MINUTES));

        // 1-1. NotificationSetting을 Member로 빠르게 조회하기 위한 Map 생성
        Map<Member, NotificationSetting> memberToSetting = settings.stream()
                .collect(Collectors.toMap(NotificationSetting::getMember, setting -> setting));

        // 2. 복용 알림 설정을 가진 회원들의 보호자 조회
        Map<Member, List<Member>> dependentToProtectors = new HashMap<>();

        for (NotificationSetting setting : settings) {
            List<Member> protectors = dependencyService.findProtectorsByDependent(setting.getMember());
            dependentToProtectors.put(setting.getMember(), protectors);
        }

        // 3. 복용 알림 설정을 가진 회원들의 복용 여부 조회
        Map<Member, List<TakingInformationItem>> takingInformationItems = new HashMap<>();
        for (Member dependent : dependentToProtectors.keySet()) {
            // 보호자가 존재하지 않는 경우 조회하지 않음
            if (dependentToProtectors.get(dependent).isEmpty()) {
                continue;
            }
            List<TakingInformationItem> items = managementService.selectCurrentTakingInformationList(dependent);
            takingInformationItems.put(dependent, items);
        }

        // 4. 복용 여부에 따라 보호자에게 알림 전송
        for (Member dependent : takingInformationItems.keySet()) {
            NotificationSetting setting = memberToSetting.get(dependent);
            List<TakingInformationItem> items = takingInformationItems.get(dependent);

            // 피보호자의 보호자들에게 알림 전송
            for (Member protector : dependentToProtectors.get(dependent)) {
                // 복용 알림 시간 타입 확인
                NotificationTimeType timeType = NotificationTimeType.determineType(setting, currentTime);

                // 복용 여부 확인(모든 약 복용 여부가 true인 경우에만 복용했다는 것으로 판단)
                boolean isTaken = checkMedicineStatus(items, timeType);

                // 복용 여부에 따라 보호자에게 알림 전송
                fcmNotificationService.sendToProtectorNotificationForTaking(
                        protector.getId(),
                        dependent.getName() + "님이 " +
                                (isTaken ? notificationMessageProvider.getTakenMessage(timeType) : notificationMessageProvider.getNotTakenMessage(timeType)),
                        ""
                );
            }
        }
    }

    private boolean checkMedicineStatus(List<TakingInformationItem> items, NotificationTimeType timeType) {
        boolean isTaking = true;

        // 복용 여부 확인(모든 약 복용 여부가 true인 경우에만 복용했다는 것으로 판단)
        for (TakingInformationItem item : items) {
            switch (timeType) {
                case MORNING -> {
                    if (item.morning() && !item.morningTaking()) isTaking = false;
                }
                case LUNCH -> {
                    if (item.lunch() && !item.lunchTaking()) isTaking = false;
                }
                case DINNER -> {
                    if (item.dinner() && !item.dinnerTaking()) isTaking = false;
                }
                case SLEEP -> {
                    if (item.sleep() && !item.sleepTaking()) isTaking = false;
                }
            }

            if (!isTaking) break;
        }
        return isTaking;
    }

    // 일관된 시간을 위해 currentTime을 매개변수로 받아 이용
    private void sendNotification(NotificationSetting setting, LocalTime currentTime) {
        // 알림 시간 타입 확인
        NotificationTimeType timeType = NotificationTimeType.determineType(setting, currentTime);

        fcmNotificationService.sendNotificationSetting(
                setting.getMember().getId(),
                notificationMessageProvider.getMedicationMessage(timeType), "");
    }
}
