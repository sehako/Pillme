package com.ssafy.pillme.history.domain;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.global.entity.BaseEntity;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.domain.Management;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class History extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_id")
    private Management management;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "information_id")
    private Information information;
    @Column(name = "taking_date")
    private LocalDate takingDate;
    @ColumnDefault(value = "false")
    private boolean morning = false;
    @ColumnDefault(value = "false")
    private boolean lunch = false;
    @ColumnDefault(value = "false")
    private boolean dinner = false;
    @ColumnDefault(value = "false")
    private boolean sleep = false;
    @Column(name = "morning_taking")
    @ColumnDefault(value = "false")
    private boolean morningTaking = false;
    @Column(name = "lunch_taking")
    @ColumnDefault(value = "false")
    private boolean lunchTaking = false;
    @Column(name = "dinner_taking")
    @ColumnDefault(value = "false")
    private boolean dinnerTaking = false;
    @Column(name = "sleep_taking")
    @ColumnDefault(value = "false")
    private boolean sleepTaking = false;

    @Builder
    private History(Long id, Management management, Member member, Information information, LocalDate takingDate,
                    boolean morning, boolean lunch, boolean dinner, boolean sleep, boolean morningTaking,
                    boolean lunchTaking, boolean dinnerTaking, boolean sleepTaking) {
        this.id = id;
        this.management = management;
        this.member = member;
        this.information = information;
        this.takingDate = takingDate;
        this.morning = morning;
        this.lunch = lunch;
        this.dinner = dinner;
        this.sleep = sleep;
        this.morningTaking = morningTaking;
        this.lunchTaking = lunchTaking;
        this.dinnerTaking = dinnerTaking;
        this.sleepTaking = sleepTaking;
    }
}
