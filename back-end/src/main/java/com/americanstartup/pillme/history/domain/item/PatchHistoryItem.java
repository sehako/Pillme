package com.americanstartup.pillme.history.domain.item;

import com.americanstartup.pillme.history.domain.dto.HistoryChangeDto;

public record PatchHistoryItem(
        Long historyId,
        boolean morningTaking,
        boolean lunchTaking,
        boolean dinnerTaking,
        boolean sleepTaking
) {
    public HistoryChangeDto toHistoryChangeDto() {
        return new HistoryChangeDto(
                morningTaking,
                lunchTaking,
                dinnerTaking,
                sleepTaking
        );
    }
}
