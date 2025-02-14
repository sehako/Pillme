package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.management.domain.Management;
import java.time.LocalDate;
import java.util.List;

public interface ManagementRepositoryCustom {
    List<Management> findByInformationDateAndMember(final LocalDate date, final Member member);

    List<Management> findByInformationDate(final LocalDate date);

    List<Management> findManagementsByInformationIdAndWriterId(final Long informationId, final Long writerId);

    List<Management> findManagementsByInformationIdAndReaderId(final Long informationId, final Long readerId);
}
