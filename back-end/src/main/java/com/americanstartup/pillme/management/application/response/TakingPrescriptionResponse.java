package com.americanstartup.pillme.management.application.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.americanstartup.pillme.management.domain.Information;
import com.americanstartup.pillme.management.domain.type.RegistrationType;
import java.time.LocalDate;

public record TakingPrescriptionResponse(
        Long informationId,
        RegistrationType registrationType,
        String diseaseName,
        String hospital,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate
) {
    public static TakingPrescriptionResponse of(
            final Information information,
            final RegistrationType registrationType
    ) {
        return new TakingPrescriptionResponse(
                information.getId(),
                registrationType,
                information.getDiseaseName(),
                information.getHospital(),
                information.getStartDate(),
                information.getEndDate()
        );
    }
}
