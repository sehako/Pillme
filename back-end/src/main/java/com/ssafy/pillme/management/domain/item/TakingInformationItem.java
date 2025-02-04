package com.ssafy.pillme.management.domain.item;

import com.ssafy.pillme.management.domain.Management;

public record TakingInformationItem(
        Long medicationId,
        String medicationName,
        Integer period,
        Integer servingSize,
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
                management.getMedication().getName(),
                management.getPeriod(),
                management.getServingSize(),
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
