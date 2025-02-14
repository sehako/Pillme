package com.ssafy.pillme.management.application.response;


import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.item.TakingInformationItem;
import java.time.LocalDate;
import java.util.List;

public record TakingDetailResponse(
        String hospital,
        String diseaseName,
        Long reader,
        LocalDate startDate,
        LocalDate endDate,
        List<TakingInformationItem> medications
) {
    public static TakingDetailResponse of(
            final Information information,
            final List<TakingInformationItem> medications) {
        return new TakingDetailResponse(
                information.getHospital(),
                information.getDiseaseName(),
                information.getReader().getId(),
                information.getStartDate(),
                information.getEndDate(),
                medications
        );
    }
}
