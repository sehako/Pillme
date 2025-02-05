package com.ssafy.pillme.history.application;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.history.infrastructure.HistoryRepository;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.Management;
import com.ssafy.pillme.management.infrastructure.InformationRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryScheduler {
    private final HistoryRepository historyRepository;
    private final InformationRepository informationRepository;

    @Scheduled(cron = "0 0 4 * * *")
    public void creatHistory() {
        LocalDate validInformationDate = LocalDate.now().minusDays(1);

        log.info("{} 복약 내역 스케줄러 동작", validInformationDate);
        List<Information> validInformation = informationRepository.findByDate(validInformationDate);

        for (Information information : validInformation) {
            List<Management> validManagements = information.getManagements();
            Member member = information.getReader();
            List<History> histories = validManagements.stream()
                    .filter(management -> !management.isDeleted())
                    .map(management -> History.builder()
                            .information(information)
                            .management(management)
                            .member(member)
                            .morning(management.isMorning())
                            .lunch(management.isLunch())
                            .dinner(management.isDinner())
                            .sleep(management.isSleep())
                            .morningTaking(management.isMorningTaking())
                            .lunchTaking(management.isLunchTaking())
                            .dinnerTaking(management.isDinnerTaking())
                            .sleepTaking(management.isSleepTaking())
                            .takingDate(validInformationDate)
                            .build())
                    .collect(Collectors.toList());
            historyRepository.saveAll(histories);
        }

    }
}
