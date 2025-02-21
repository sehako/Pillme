package com.ssafy.pillme.notification.infrastructure.repository;

import com.ssafy.pillme.notification.domain.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Integer> {
    List<FCMToken> findAllByMemberIdAndDeletedIsFalse(Long memberId);

    boolean existsByMemberIdAndTokenAndDeletedIsFalse(@Param("memberId") Long memberId, @Param("token") String token);

    Optional<FCMToken> findByMemberIdAndTokenAndDeletedIsFalse(@Param("memberId") Long memberId, @Param("token") String token);

    // N+1 문제 해결을 위한 fetch join
    @Query("SELECT f FROM FCMToken f " +
            "JOIN FETCH f.member " +
            "WHERE f.token = :token AND f.deleted = false")
    Optional<FCMToken> findByTokenAndDeletedIsFalse(String token);

    // N+1 문제 해결을 위한 fetch join
    @Query("SELECT f FROM FCMToken f " +
            "JOIN FETCH f.member " +
            "WHERE f.token = :token AND f.member.id = :memberId AND f.deleted = true")
    Optional<FCMToken> findByTokenAndMemberIdAndDeletedIsTrue(String token, Long memberId);
}
