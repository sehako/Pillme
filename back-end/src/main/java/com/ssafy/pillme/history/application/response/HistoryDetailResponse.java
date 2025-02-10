package com.ssafy.pillme.history.application.response;

import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.management.domain.Management;

public record HistoryDetailResponse(
        Long historyId,
        String medicationName,
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
