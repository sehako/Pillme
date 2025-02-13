package com.ssafy.pillme.management.domain.item;

import com.ssafy.pillme.management.domain.Management;

public record TakingInformationItem(
        Long managementId,
        String medicationName,
        Integer period,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep,
        boolean morningTaking,
        boolean lunchTaking,
        boolean dinnerTaking,
        boolean sleepTaking
) {
    public static TakingInformationItem from(final Management management) {
        return new TakingInformationItem(
                management.getId(),
                management.getMedicationName(),
                management.getPeriod(),
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
