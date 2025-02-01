package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.item.TakingInformationItem;
import java.util.List;

public record ChangeTakingInformationRequest(
        List<TakingInformationItem> medications
) {
}
