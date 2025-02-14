package com.ssafy.pillme.management.infrastructure;

import static com.ssafy.pillme.management.domain.QManagement.management;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.pillme.auth.domain.entity.QMember;
import com.ssafy.pillme.management.domain.Management;
import com.ssafy.pillme.management.domain.QInformation;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManagementRepositoryImpl implements ManagementRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Management> findByInformationDateAndMember(LocalDate date, Long memberId) {
        QInformation information = QInformation.information;
        return queryFactory.selectFrom(management)
                .leftJoin(management.information, information).fetchJoin()
                .where(information.reader.id.eq(memberId)
                        .and(information.startDate.loe(date))
                        .and(information.endDate.goe(date))
                        .and(information.deleted.eq(false)))
                .fetch();
    }

    @Override
    public List<Management> findByInformationDate(LocalDate date) {
        QInformation information = QInformation.information;
        return queryFactory.selectFrom(management)
                .leftJoin(management.information, information).fetchJoin()
                .where(information.startDate.loe(date)
                        .and(information.endDate.goe(date))
                        .and(information.deleted.eq(false)))
                .fetch();
    }

    @Override
    public List<Management> findManagementsByInformationIdAndWriterId(Long informationId, Long memberId) {
        QInformation information = QInformation.information;
        QMember member = QMember.member;
        return queryFactory
                .selectFrom(management)
                .leftJoin(management.information, information).fetchJoin()
                .leftJoin(information.writer, member).fetchJoin()
                .where(information.writer.id.eq(memberId).and(information.id.eq(informationId)))
                .fetch();
    }

    @Override
    public List<Management> findManagementsByInformationIdAndReaderId(Long informationId, Long readerId) {
        QInformation information = QInformation.information;
        QMember member = QMember.member;
        return queryFactory
                .selectFrom(management)
                .leftJoin(management.information, information).fetchJoin()
                .leftJoin(information.reader, member).fetchJoin()
                .where(information.reader.id.eq(readerId).and(information.id.eq(informationId)))
                .fetch();
    }
}
