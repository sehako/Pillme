package com.ssafy.pillme.history.application;

import com.ssafy.pillme.history.infrastructure.HistoryRepository;
import com.ssafy.pillme.management.infrastructure.ManagementRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryScheduler {
    private final HistoryRepository historyRepository;
    private final ManagementRepository managementRepository;

    //    @Scheduled(cron = "0 0 2 * * *")
    @Scheduled(cron = "0 * * * * *")
    public void creatHistory() {
        LocalDate validInformationDate = LocalDate.now().minusDays(1);

        log.info("{} 복약 내역 스케줄러 동작", validInformationDate);
//        List<Management> validManagements = managementRepository.findByInformationDate(validInformationDate);
//
//        List<History> histories = validManagements.stream()
//                .filter(management -> !management.isDeleted())
//                .map(management -> {
//                    History history = History.builder()
//                            .information(management.getInformation())
//                            .management(management)
//                            .member(management.getInformation().getReader())
//                            .morning(management.isMorning())
//                            .lunch(management.isLunch())
//                            .dinner(management.isDinner())
//                            .sleep(management.isSleep())
//                            .morningTaking(management.isMorningTaking())
//                            .lunchTaking(management.isLunchTaking())
//                            .dinnerTaking(management.isDinnerTaking())
//                            .sleepTaking(management.isSleepTaking())
//                            .takingDate(validInformationDate)
//                            .build();
//
//                    management.resetTakingInformation();
//                    return history;
//                })
//                .toList();
//
//        historyRepository.saveAll(histories);

    }
}
