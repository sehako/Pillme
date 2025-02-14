package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.management.domain.Information;
import java.time.LocalDate;
import java.util.List;

public interface InformationRepositoryCustom {
    List<Information> findAllByDateAndMemberId(LocalDate date, Long memberId);
}
