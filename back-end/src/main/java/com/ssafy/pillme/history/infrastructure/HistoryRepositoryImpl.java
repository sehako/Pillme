package com.ssafy.pillme.history.infrastructure;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.pillme.history.domain.History;
import com.ssafy.pillme.history.domain.QHistory;
import com.ssafy.pillme.history.domain.dto.HistorySearchFilter;
import com.ssafy.pillme.management.domain.QInformation;
import com.ssafy.pillme.management.domain.QManagement;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HistoryRepositoryImpl implements HistoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<History> findHistoryByCondition(final HistorySearchFilter filter) {
        QHistory history = QHistory.history;
        QManagement management = QManagement.management;
        QInformation information = QInformation.information;

        return queryFactory.selectFrom(history)
                .leftJoin(history.management, management).fetchJoin()
                .leftJoin(management.information, information).fetchJoin()
                .where(buildConditions(filter, history))
                .orderBy(history.takingDate.asc())
                .fetch();
    }

    private BooleanBuilder buildConditions(final HistorySearchFilter filter, final QHistory history) {
        BooleanBuilder builder = new BooleanBuilder();

        if (filter.startDate() != null) {
            builder.and(history.takingDate.goe(filter.startDate()));
        }
        if (filter.endDate() != null) {
            builder.and(history.takingDate.loe(filter.endDate()));
        }
        if (filter.hospital() != null) {
            builder.and(history.management.information.hospital.like(filter.hospital()));
        }
        if (filter.diseaseName() != null) {
            builder.and(history.management.information.diseaseName.like(filter.diseaseName()));
        }

        return builder;
    }
}
