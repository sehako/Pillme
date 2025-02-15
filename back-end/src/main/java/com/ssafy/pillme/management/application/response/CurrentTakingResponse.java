package com.ssafy.pillme.management.application.response;

import com.ssafy.pillme.management.domain.Management;

public record CurrentTakingResponse(
        Long managementId,
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
    public static CurrentTakingResponse from(final Management management) {
        return new CurrentTakingResponse(
                management.getId(),
                management.getMedicationName(),
                management.isMorning(),
                management.isLunch(),
                management.isDinner(),
                management.isSleep(),
                management.isMorningTaking(),
                management.isLunchTaking(),
                management.isDinnerTaking(),
                management.isSleepTaking()
        );
    }
}
