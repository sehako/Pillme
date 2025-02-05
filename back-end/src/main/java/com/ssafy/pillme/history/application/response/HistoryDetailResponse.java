package com.ssafy.pillme.history.application.response;

import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.search.domain.Medication;

public record HistoryDetailResponse(
        Long historyId,
        String medication,
        String company,
        String image,
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
        Medication medication = history.getManagement().getMedication();
        return new HistoryDetailResponse(
                history.getId(),
                medication.getName(),
                medication.getCompany(),
                medication.getImage(),
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
