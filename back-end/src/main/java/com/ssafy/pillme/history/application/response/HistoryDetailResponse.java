package com.ssafy.pillme.history.application.response;

public record HistoryDetailResponse(
        Long historyId,
        Long informationId,
        String hospital,
        String diseaseName,

) {
}
