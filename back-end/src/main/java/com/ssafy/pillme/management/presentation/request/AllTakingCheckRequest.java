package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.type.TakingType;

public record AllTakingCheckRequest(
        TakingType time
) {
}
