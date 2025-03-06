package com.americanstartup.pillme.management.domain.item;

import com.americanstartup.pillme.management.domain.Management;

public record TakingInformationItem(
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
    public static TakingInformationItem from(final Management management) {
        return new TakingInformationItem(
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
