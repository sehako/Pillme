package com.americanstartup.pillme.management.presentation.request;

import java.util.List;

public record DeleteManagementRequest(
        List<Long> managementList
) {
}
