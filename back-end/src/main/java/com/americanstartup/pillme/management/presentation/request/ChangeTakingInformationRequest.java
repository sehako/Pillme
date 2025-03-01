package com.americanstartup.pillme.management.presentation.request;

import com.americanstartup.pillme.management.domain.item.ChangeManagementItem;
import java.util.List;

public record ChangeTakingInformationRequest(
        List<ChangeManagementItem> medications
) {
}
