package com.ssafy.pillme.history.application;

import com.ssafy.pillme.history.application.response.HistorySearchResponse;
import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.history.domain.dto.HistorySearchFilter;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HistoryService {
    private final InformationRepository informationRepository;
    private final HistoryRepository historyRepository;

    @Scheduled(cron = "0 0 4 * * *")
    public void creatHistory() {
        LocalDate validInformationDate = LocalDate.now().minusDays(1);

        List<Information> validInformation = informationRepository.findByDate(validInformationDate);

        for (Information information : validInformation) {
            List<Management> validManagements = information.getManagements();
            validManagements.stream()
                    .filter(management -> !management.isDeleted())
                    .forEach(management ->
                            historyRepository.save(History.builder()
                                    .management(management)
                                    .morning(management.isMorningTaking())
                                    .lunch(management.isLunchTaking())
                                    .dinner(management.isDinnerTaking())
                                    .sleep(management.isSleepTaking())
                                    .takingDate(validInformationDate)
                                    .build())
                    );
        }
    }

    public List<HistorySearchResponse> selectHistoryByFilter(final HistorySearchFilter filter) {
        List<History> historyByCondition = historyRepository.findHistoryByCondition(filter);

        return historyByCondition.stream().map(HistorySearchResponse::of).collect(Collectors.toList());
    }
}
