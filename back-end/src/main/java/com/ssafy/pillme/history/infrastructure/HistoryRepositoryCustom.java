package com.ssafy.pillme.history.infrastructure;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.history.domain.dto.HistorySearchFilter;
import java.time.LocalDate;
import java.util.List;

public interface HistoryRepositoryCustom {
    List<History> findHistoryByCondition(final HistorySearchFilter filter);

    List<History> findHistoryByInformationId(final Long informationId);

    List<History> findHistoryByDate(final Member member, final LocalDate date);
}
