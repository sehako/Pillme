package com.ssafy.pillme.management.domain;

import com.ssafy.pillme.global.entity.BaseEntity;
import com.ssafy.pillme.management.domain.item.ChangeManagementItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "management")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Management extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "medication_name")
    private String medicationName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "information_id")
    private Information information;
    private Integer period;
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
    public Management(Long id, String medicationName, Information information, Integer period,
                      boolean morning, boolean lunch, boolean dinner, boolean sleep, boolean morningTaking,
                      boolean lunchTaking, boolean dinnerTaking, boolean sleepTaking) {
        this.id = id;
        this.medicationName = medicationName;
        this.information = information;
        this.period = period;
        this.morning = morning;
        this.lunch = lunch;
        this.dinner = dinner;
        this.sleep = sleep;
        this.morningTaking = morningTaking;
        this.lunchTaking = lunchTaking;
        this.dinnerTaking = dinnerTaking;
        this.sleepTaking = sleepTaking;
    }


    public void changeTakingInformation(final ChangeManagementItem item) {
        this.period = item.period();
        this.morning = item.morning();
        this.dinner = item.dinner();
        this.sleep = item.sleep();
    }

    public void checkMorningTaking() {
        this.morningTaking = true;
    }

    public void checkLunchTaking() {
        this.lunchTaking = true;
    }

    public void checkDinnerTaking() {
        this.dinnerTaking = true;
    }

    public void checkSleepTaking() {
        this.sleepTaking = true;
    }

    public void resetTakingInformation() {
        this.morningTaking = false;
        this.lunchTaking = false;
        this.dinnerTaking = false;
        this.sleepTaking = false;
    }
}
