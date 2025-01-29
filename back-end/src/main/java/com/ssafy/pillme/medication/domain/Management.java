package com.ssafy.pillme.medication.domain;

import com.ssafy.pillme.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "management")
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
    private boolean breakfast;
    private boolean lunch;
    private boolean dinner;
    private boolean sleep;
    private boolean breakfastTaking;
    private boolean lunchTaking;
    private boolean dinnerTaking;
    private boolean sleepTaking;
}
