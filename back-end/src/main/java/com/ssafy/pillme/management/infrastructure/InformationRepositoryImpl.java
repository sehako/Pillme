package com.ssafy.pillme.management.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.QInformation;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InformationRepositoryImpl implements InformationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Information> findAllByDateAndMemberId(LocalDate date, Long memberId) {
        QInformation information = QInformation.information;

        return queryFactory.selectFrom(information)
                .where(information.deleted.eq(false)
                        .and(information.reader.id.eq(memberId))
                        .and(information.startDate.loe(date))
                        .and(information.endDate.goe(date)))
                .fetch();
    }
}
