package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.Information;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public record MedicationRegisterRequest(
        String hospital,
        String diseaseName,
        Long reader,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        LocalDate endDate,
        boolean isSupplement,
        List<TakingInfoRequest> takingInfoRequest
) {
    public Information toInformation() {
        return Information.builder()
                .hospital(hospital)
                .diseaseName(diseaseName)
                .startDate(startDate)
                .endDate(endDate)
                .supplement(isSupplement)
                .build();
    }
}
