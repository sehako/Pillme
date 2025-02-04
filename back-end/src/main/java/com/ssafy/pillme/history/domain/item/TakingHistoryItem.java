package com.ssafy.pillme.history.domain.item;

import com.ssafy.pillme.history.domain.History;

public record TakingHistoryItem(
        String medication,
        String company,
        String image,
        boolean morning,
        boolean lunch,
        boolean dinner,
        boolean sleep,
        boolean morningTaking,
        boolean lunchTaking,
        boolean dinnerTaking,
        boolean sleepTaking
) {
    public static TakingHistoryItem of(final History history) {
        return null;
    }
}
