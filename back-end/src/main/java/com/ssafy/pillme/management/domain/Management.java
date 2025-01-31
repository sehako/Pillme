package com.ssafy.pillme.management.domain;

import com.ssafy.pillme.global.entity.BaseTimeEntity;
import com.ssafy.pillme.search.domain.Medication;
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
public class Management extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    private Medication medication;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "information_id")
    private Information information;
    @Column(name = "serving_size")
    private Integer servingSize;
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
    private Management(Long id, Medication medication, Information information, Integer servingSize, Integer period,
                       boolean morning, boolean lunch, boolean dinner, boolean sleep, boolean morningTaking,
                       boolean lunchTaking, boolean dinnerTaking, boolean sleepTaking) {
        this.id = id;
        this.medication = medication;
        this.information = information;
        this.servingSize = servingSize;
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
}
