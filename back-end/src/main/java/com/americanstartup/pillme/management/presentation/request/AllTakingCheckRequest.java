package com.americanstartup.pillme.management.presentation.request;

import com.americanstartup.pillme.management.domain.type.TakingType;

public record AllTakingCheckRequest(
        TakingType time
) {
}
