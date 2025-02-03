package com.ssafy.pillme.management.application.response;

import com.ssafy.pillme.auth.domain.entity.Member;
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
    public static PrescriptionResponse of(
            final Information information,
            final Member member
    ) {
        int status;
        Member reader = information.getReader();
        Member writer = information.getWriter();
        if (member.getId().equals(writer.getId()) && member.getId().equals(reader.getId())) {
            status = 1;
        } else if (member.getId().equals(reader.getId())) {
            status = 2;
        } else {
            status = 3;
        }

        return new PrescriptionResponse(
                information.getId(),
                status,
                information.getHospital(),
                information.getDiseaseName(),
                information.getStartDate(),
                information.getEndDate(),
                information.isSupplement()
        );
    }
}
