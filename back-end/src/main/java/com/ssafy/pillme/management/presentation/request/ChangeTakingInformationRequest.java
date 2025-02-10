package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.item.ChangeManagementItem;
import java.util.List;

public record ChangeTakingInformationRequest(
        List<ChangeManagementItem> managementList
) {
}
