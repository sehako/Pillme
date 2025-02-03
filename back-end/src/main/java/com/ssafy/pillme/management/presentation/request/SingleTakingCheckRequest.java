package com.ssafy.pillme.management.presentation.request;

public record SingleTakingCheckRequest(
        Long medicationId,
        String time
) {
}
