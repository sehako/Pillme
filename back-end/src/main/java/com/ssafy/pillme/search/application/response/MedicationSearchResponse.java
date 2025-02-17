package com.ssafy.pillme.search.application.response;

import com.ssafy.pillme.search.domain.MedicationDocument;

public record MedicationSearchResponse(
        String name,
        String company,
        String image
) {
    public static MedicationSearchResponse of(
            final MedicationDocument medication
    ) {
        return new MedicationSearchResponse(
                medication.getName(),
                medication.getCompany(),
                medication.getImage()
        );
    }
}
