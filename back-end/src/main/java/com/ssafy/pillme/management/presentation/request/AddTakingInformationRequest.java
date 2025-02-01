package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.item.TakingInformationItem;

public record AddTakingInformationRequest(
        Long infoId,
        TakingInformationItem medications
) {
}
