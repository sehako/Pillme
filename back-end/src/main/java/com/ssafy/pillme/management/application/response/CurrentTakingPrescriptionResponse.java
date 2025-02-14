package com.ssafy.pillme.management.application.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.pillme.management.domain.Information;
import java.time.LocalDate;

public record CurrentTakingPrescriptionResponse(
        Long informationId,
        String diseaseName,
        String hospital,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
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
