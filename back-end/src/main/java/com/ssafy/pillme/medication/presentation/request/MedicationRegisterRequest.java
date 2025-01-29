package com.ssafy.pillme.medication.presentation.request;

import com.ssafy.pillme.medication.domain.Information;
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
        List<MedicationInfo> medicationInfo
) {
    Information toInformation() {
        return Information.builder()
                .hospital(hospital)
                .diseaseName(diseaseName)
                .startDate(startDate)
                .endDate(endDate)
                .supplement(isSupplement)
                .build();
    }
}
