package com.ssafy.pillme.history.domain.dto;

import java.time.LocalDate;

public record HistorySearchFilter(
        LocalDate startDate,
        LocalDate endDate,
        String hospital,
        String diseaseName,
        Long memberId
) {
    public static HistorySearchFilter of(LocalDate startDate, LocalDate endDate, String hospital, String diseaseName,
                                         Long memberId) {
        return new HistorySearchFilter(startDate, endDate, hospital, diseaseName, memberId);
    }
}
