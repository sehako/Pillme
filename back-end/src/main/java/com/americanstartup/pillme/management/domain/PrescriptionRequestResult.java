package com.americanstartup.pillme.management.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PrescriptionRequestResult(
        @JsonProperty(value = "matched_drug")
        String matchedDrug
) {
    public Management toManagement() {
        return Management.builder()
                .medicationName(this.matchedDrug)
                .build();
    }
}
