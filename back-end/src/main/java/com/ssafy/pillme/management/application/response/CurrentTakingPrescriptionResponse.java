package com.ssafy.pillme.management.application.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.type.RegistrationType;
import java.time.LocalDate;

public record CurrentTakingPrescriptionResponse(
        Long informationId,
        RegistrationType registrationType,
        String diseaseName,
        String hospital,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate
) {
    public static CurrentTakingPrescriptionResponse of(
            final Information information,
            final RegistrationType registrationType
    ) {
        return new CurrentTakingPrescriptionResponse(
                information.getId(),
                registrationType,
                information.getDiseaseName(),
                information.getHospital(),
                information.getStartDate(),
                information.getEndDate()
        );
    }
}
