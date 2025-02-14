package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.item.TakingSettingItem;

public record AddTakingInformationRequest(
        String medicationName,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep
) {
    public TakingSettingItem toItem() {
        return new TakingSettingItem(
                medicationName,
                morning,
                lunch,
                dinner,
                sleep
        );
    }
}
