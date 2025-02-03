package com.ssafy.pillme.management.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.QInformation;
import com.ssafy.pillme.management.domain.QManagement;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InformationRepositoryImpl implements InformationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Information> findByDate(LocalDate date) {
        QInformation information = QInformation.information;
        QManagement management = QManagement.management;
        return queryFactory.selectFrom(information)
                .leftJoin(information.managements, management).fetchJoin()
                .where(information.startDate.loe(date)
                        .and(information.endDate.goe(date))
                        .and(information.deleted.eq(false)))
                .fetch();
    }
}
