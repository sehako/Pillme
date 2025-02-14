package com.ssafy.pillme.management.application.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.management.application.util.StatusCalculator;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.type.RegistrationType;
import java.time.LocalDate;

public record PrescriptionResponse(
        Long infoId,
        RegistrationType registrationType,
        String hospital,
        String diseaseName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate
) {
    public static PrescriptionResponse of(
            final Information information,
            final Member member
    ) {

        return new PrescriptionResponse(
                information.getId(),
                StatusCalculator.calculateStatus(information, member),
                information.getHospital(),
                information.getDiseaseName(),
                information.getStartDate(),
                information.getEndDate()
        );
    }
}
