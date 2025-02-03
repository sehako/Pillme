package com.ssafy.pillme.management.domain.item;

import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.Management;
import com.ssafy.pillme.search.domain.Medication;

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
    public Management toManagement(final Medication medication, final Information information) {
        return Management.builder()
                .medication(medication)
                .information(information)
                .period(period)
                .servingSize(servingSize)
                .morning(morning)
                .lunch(lunch)
                .dinner(dinner)
                .sleep(sleep)
                .build();
    }

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
