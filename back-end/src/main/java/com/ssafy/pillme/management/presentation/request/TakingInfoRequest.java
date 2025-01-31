package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.Management;
import com.ssafy.pillme.search.domain.Medication;

public record TakingInfoRequest(
        Long medicationId,
        Integer period,
        Integer servingSize,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep
) {
    public Management toManagement(Medication medication, Information information) {
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
}
