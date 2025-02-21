package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.item.TakingSettingItem;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public record TakingInformationRegisterRequest(
        String hospital,
        String diseaseName,
        Long reader,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        LocalDate endDate,
        List<TakingSettingItem> medications
) {
    public Information toInformation(Member writer, Member reader) {
        return Information.builder()
                .hospital(hospital)
                .writer(writer)
                .reader(reader)
                .diseaseName(diseaseName)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}