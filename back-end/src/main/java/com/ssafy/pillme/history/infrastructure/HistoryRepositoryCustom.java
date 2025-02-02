package com.ssafy.pillme.history.infrastructure;

import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.history.domain.dto.HistorySearchFilter;
import java.util.List;

public interface HistoryRepositoryCustom {
    List<History> findHistoryByCondition(final HistorySearchFilter filter);
}
