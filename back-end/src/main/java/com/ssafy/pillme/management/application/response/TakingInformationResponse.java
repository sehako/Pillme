package com.ssafy.pillme.management.application.response;

import com.ssafy.pillme.management.domain.Management;
import java.util.List;

public record TakingInformationResponse(
        Long medicationId,
        String medicationName,
        Integer period,
        Integer servingSize,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep
) {
    public static List<TakingInformationResponse> from(List<Management> managements) {
        return managements.stream()
                .map(Management::toResponse)
                .toList();
    }
}
