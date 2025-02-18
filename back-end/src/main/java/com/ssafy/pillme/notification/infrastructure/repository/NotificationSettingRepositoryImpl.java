package com.ssafy.pillme.notification.infrastructure.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.TimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.pillme.auth.domain.entity.QMember;
import com.ssafy.pillme.notification.domain.entity.NotificationSetting;
import com.ssafy.pillme.notification.domain.entity.QNotificationSetting;

import java.time.LocalTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationSettingRepositoryImpl implements NotificationSettingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<NotificationSetting> findSettingsForCurrentTime(LocalTime currentTime) {
        // QNotificationSetting : QueryDSL에서 사용할 Entity
        QNotificationSetting setting = QNotificationSetting.notificationSetting;
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(setting)
                .join(setting.member, member).fetchJoin()
                .where(
                        timeMatches(setting.morning, currentTime)
                                .or(timeMatches(setting.lunch, currentTime))
                                .or(timeMatches(setting.dinner, currentTime))
                                .or(timeMatches(setting.sleep, currentTime))
                )
                .fetch();
    }

    // 사용자에게 시간과 분에 대해서만 입력받아 알림 설정을 하기 때문에 시간과 분이 일치하는지 확인.
    // TimePath : QueryDSL에서 시간을 다루기 위한 클래스
    // Expressions.numberTemplate : QueryDSL의 메서드로 SQL 함수를 사용할 수 있게 함
    // Integer.class : 반환 타입이 Integer임을 지정
    // hour({0}) : 시간을 추출하는 SQL 함수({0}은 첫 번째 인자를 의미)
    // timePath : {0}에 해당하는 인자
    private BooleanExpression timeMatches(TimePath<LocalTime> timePath, LocalTime currentTime) {
        return Expressions.numberTemplate(Integer.class, "hour({0})", timePath).eq(currentTime.getHour())
                .and(Expressions.numberTemplate(Integer.class, "minute({0})", timePath).eq(currentTime.getMinute()));
    }
}
