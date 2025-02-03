package com.ssafy.pillme.history.domain.dto;

import java.time.LocalDate;

public record HistorySearchFilter(
        LocalDate startDate,
        LocalDate endDate,
        String hospital,
        String diseaseName
) {
    public static HistorySearchFilter of(LocalDate startDate, LocalDate endDate, String hospital, String diseaseName) {
        return new HistorySearchFilter(startDate, endDate, hospital, diseaseName);
    }
}
