package com.ssafy.pillme.history.application.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.management.domain.Management;
import java.time.LocalDate;

public record HistoryDetailResponse(
        Long historyId,
        String medicationName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate takingDate,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep,
        boolean morningTaking,
        boolean lunchTaking,
        boolean dinnerTaking,
        boolean sleepTaking
) {
    public static HistoryDetailResponse of(final History history) {
        Management management = history.getManagement();
        return new HistoryDetailResponse(
                history.getId(),
                management.getMedicationName(),
                history.getTakingDate(),
                history.isMorning(),
                history.isLunch(),
                history.isDinner(),
                history.isSleep(),
                history.isMorningTaking(),
                history.isLunchTaking(),
                history.isDinnerTaking(),
                history.isSleepTaking()
        );
    }
}
