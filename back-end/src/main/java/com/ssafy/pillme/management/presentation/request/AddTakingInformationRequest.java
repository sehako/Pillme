package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.item.TakingSettingItem;

public record AddTakingInformationRequest(
        TakingSettingItem medication
) {
}
