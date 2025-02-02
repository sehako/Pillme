package com.ssafy.pillme.history.application.response;

import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.Management;
import java.time.LocalDate;

public record HistorySearchResponse(
        Long historyId,
        int status,
        String diseaseName,
        String hospital,
        LocalDate takingDate
) {
    public static HistorySearchResponse of(final History history) {
        Management management = history.getManagement();
        Information information = management.getInformation();
        return new HistorySearchResponse(
                history.getId(),
                1,
                information.getDiseaseName(),
                information.getHospital(),
                history.getTakingDate()
        );
    }
}
