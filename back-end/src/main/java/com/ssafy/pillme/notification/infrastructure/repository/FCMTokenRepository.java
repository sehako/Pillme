package com.ssafy.pillme.notification.infrastructure.repository;

import com.ssafy.pillme.notification.domain.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Integer> {
}
