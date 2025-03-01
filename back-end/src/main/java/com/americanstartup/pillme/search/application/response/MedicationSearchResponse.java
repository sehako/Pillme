package com.americanstartup.pillme.search.application.response;

import com.americanstartup.pillme.search.domain.MedicationDocument;

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
