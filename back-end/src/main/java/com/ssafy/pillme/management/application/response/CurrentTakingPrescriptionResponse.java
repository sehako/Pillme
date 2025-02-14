package com.ssafy.pillme.management.application.response;

import com.ssafy.pillme.management.domain.Information;
import java.time.LocalDate;

public record CurrentTakingPrescriptionResponse(
        Long informationId,
        String diseaseName,
        String hospital,
        LocalDate startDate,
        LocalDate endDate
) {
    public static CurrentTakingPrescriptionResponse of(
            final Information information
    ) {
        return new CurrentTakingPrescriptionResponse(
                information.getId(),
                information.getDiseaseName(),
                information.getHospital(),
                information.getStartDate(),
                information.getEndDate()
        );
    }
}
