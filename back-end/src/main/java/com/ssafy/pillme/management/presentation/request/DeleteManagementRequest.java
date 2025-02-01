package com.ssafy.pillme.management.presentation.request;

import java.util.List;

public record DeleteManagementRequest(
        List<Long> medications
) {
}
