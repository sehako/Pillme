package com.ssafy.pillme.management.domain.item;

import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.Management;

public record TakingSettingItem(
        String medicationName,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep
) {
    public Management toManagement(final Information information) {
        return Management.builder()
                .medicationName(medicationName)
                .information(information)
                .morning(morning)
                .lunch(lunch)
                .dinner(dinner)
                .sleep(sleep)
                .build();
    }
}
