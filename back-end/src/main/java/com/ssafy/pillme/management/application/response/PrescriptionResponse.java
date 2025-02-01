package com.ssafy.pillme.management.application.response;

import com.ssafy.pillme.management.domain.Information;
import java.time.LocalDate;

public record PrescriptionResponse(
        Long infoId,
        int status,
        String hospital,
        String diseaseName,
        LocalDate startDate,
        LocalDate endDate,
        boolean isSupplement
) {
    public static PrescriptionResponse of(final Information information) {
        return new PrescriptionResponse(
                information.getId(),
                1,
                information.getHospital(),
                information.getDiseaseName(),
                information.getStartDate(),
                information.getEndDate(),
                information.isSupplement()
        );
    }
}
