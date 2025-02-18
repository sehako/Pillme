package com.ssafy.pillme.management.domain;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.global.entity.BaseEntity;
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

@Entity
@Getter
@Table(name = "information")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Information extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader")
    private Member reader;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer")
    private Member writer;
    private String hospital;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "disease_name")
    private String diseaseName;
    private boolean requested = false;

    @Builder
    public Information(Member reader, Member writer, String hospital, LocalDate startDate, LocalDate endDate,
                       String diseaseName, boolean requested) {
        this.reader = reader;
        this.writer = writer;
        this.hospital = hospital;
        this.startDate = startDate;
        this.endDate = endDate;
        this.diseaseName = diseaseName;
        this.requested = requested;
    }

    public void dependencyBeforeSetting() {
        this.requested = true;
    }

    public void dependencyAddSetting() {
        this.requested = false;
    }
}
