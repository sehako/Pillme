package com.ssafy.pillme.medication.presentation.request;

public record MedicationInfo(
        Long medicationId,
        Integer period,
        Integer servingSize,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep
) {
}
