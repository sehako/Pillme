package com.ssafy.pillme.history.domain.dto;

import java.time.LocalDate;

public record HistorySearchFilter(
        LocalDate startDate,
        LocalDate endDate,
        String hospital
) {
    public static HistorySearchFilter of(LocalDate startDate, LocalDate endDate, String hospital) {
        return new HistorySearchFilter(startDate, endDate, hospital);
    }
}
