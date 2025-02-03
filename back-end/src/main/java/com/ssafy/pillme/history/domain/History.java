package com.ssafy.pillme.history.domain;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.global.entity.BaseEntity;
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
    @Column(name = "taking_date")
    private LocalDate takingDate;
    @Column(name = "morning_taking")
    @ColumnDefault(value = "false")
    private boolean morning = false;
    @Column(name = "lunch_taking")
    @ColumnDefault(value = "false")
    private boolean lunch = false;
    @Column(name = "dinner_taking")
    @ColumnDefault(value = "false")
    private boolean dinner = false;
    @Column(name = "sleep_taking")
    @ColumnDefault(value = "false")
    private boolean sleep = false;

    @Builder
    private History(Long id, Management management, LocalDate takingDate, boolean morning,
                    boolean lunch,
                    boolean dinner, boolean sleep) {
        this.id = id;
        this.management = management;
        this.takingDate = takingDate;
        this.morning = morning;
        this.lunch = lunch;
        this.dinner = dinner;
        this.sleep = sleep;
    }
}
