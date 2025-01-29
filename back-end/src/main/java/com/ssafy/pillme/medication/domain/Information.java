package com.ssafy.pillme.medication.domain;

import com.ssafy.pillme.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "information")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Information extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hospital;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "disease_name")
    private String diseaseName;
    private boolean supplement;

    @Builder
    public Information(Long id, String hospital, LocalDate startDate, LocalDate endDate, String diseaseName,
                       boolean supplement) {
        this.id = id;
        this.hospital = hospital;
        this.startDate = startDate;
        this.endDate = endDate;
        this.diseaseName = diseaseName;
        this.supplement = supplement;
    }
}
