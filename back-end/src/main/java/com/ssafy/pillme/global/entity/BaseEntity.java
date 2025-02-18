package com.ssafy.pillme.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.sql.Timestamp;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Getter
@MappedSuperclass
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Timestamp createdAt;
    @LastModifiedDate
    @Column(name = "modified_at", nullable = false)
    private Timestamp modifiedAt;
    @Column(nullable = false)
    @ColumnDefault(value = "false")
    private boolean deleted = false;

    public void delete() {
        this.deleted = true;
    }

    public void activate() {
        this.deleted = false;
    }
}