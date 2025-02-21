package com.ssafy.pillme.history.application;

import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.history.infrastructure.HistoryRepository;
import com.ssafy.pillme.management.application.ManagementService;
import com.ssafy.pillme.management.domain.Management;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class HistoryScheduler {
    private final HistoryRepository historyRepository;
    private final ManagementService managementService;

    @Scheduled(cron = "0 0 2 * * *")
    public void creatHistory() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Management> validManagements = managementService.selectYesterdayManagementList();

        List<History> histories = validManagements.stream()
                .filter(management -> !management.isDeleted())
                .map(management -> {
                    History history = History.builder()
                            .information(management.getInformation())
                            .management(management)
                            .member(management.getInformation().getReader())
                            .morning(management.isMorning())
                            .lunch(management.isLunch())
                            .dinner(management.isDinner())
                            .sleep(management.isSleep())
                            .morningTaking(management.isMorningTaking())
                            .lunchTaking(management.isLunchTaking())
                            .dinnerTaking(management.isDinnerTaking())
                            .sleepTaking(management.isSleepTaking())
                            .takingDate(yesterday)
                            .build();

                    management.resetTakingInformation();
                    return history;
                })
                .toList();

        historyRepository.saveAll(histories);
    }
}
