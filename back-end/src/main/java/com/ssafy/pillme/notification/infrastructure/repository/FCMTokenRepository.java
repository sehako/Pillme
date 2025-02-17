package com.ssafy.pillme.notification.infrastructure.repository;

import com.ssafy.pillme.notification.domain.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Integer> {
    List<FCMToken> findAllByMemberIdAndDeletedIsFalse(Long memberId);

    boolean existsByMemberIdAndTokenAndDeletedIsFalse(@Param("memberId") Long memberId, @Param("token") String token);

    Optional<FCMToken> findByMemberIdAndTokenAndDeletedIsFalse(@Param("memberId") Long memberId, @Param("token") String token);

    Optional<FCMToken> findByTokenAndDeletedIsFalse(String token);

    Optional<FCMToken> findByTokenAndMemberIdAndDeletedIsTrue(String token, Long memberId);
}
