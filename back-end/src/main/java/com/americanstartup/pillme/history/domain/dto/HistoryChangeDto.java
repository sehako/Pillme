package com.americanstartup.pillme.history.domain.dto;

public record HistoryChangeDto(
        boolean morningTaking,
        boolean lunchTaking,
        boolean dinnerTaking,
        boolean sleepTaking
) {
}
