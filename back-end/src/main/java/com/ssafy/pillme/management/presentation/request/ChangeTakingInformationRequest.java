package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.item.TakingSettingItem;
import java.util.List;

public record ChangeTakingInformationRequest(
        List<TakingSettingItem> medications
) {
}
