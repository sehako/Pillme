package com.ssafy.pillme.dependency.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.dependency.domain.entity.Dependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DependencyRepository extends JpaRepository<Dependency, Long> {
    // 회원의 삭제되지 않은 모든 관계 조회
    List<Dependency> findAllByProtectorIdAndDeletedIsFalse(Long protectorId);

    Optional<Dependency> findByIdAndDeletedIsFalse(Long dependencyId);

    // 두 회원 간의 관계 조회
    @Query("SELECT d FROM Dependency d WHERE " +
            "((d.protector.id = :firstMemberId AND d.dependent.id = :secondMemberId) OR " +
            "(d.protector.id = :secondMemberId AND d.dependent.id = :firstMemberId)) AND " +
            "d.deleted = false")
    Optional<Dependency> findByMemberIdsAndDeletedIsFalse(@Param("firstMemberId") Long firstMemberId, @Param("secondMemberId") Long secondMemberId);

    // 회원의 모든 보호자 조회
    @Query("SELECT d.protector FROM Dependency d WHERE d.dependent = :dependent AND d.deleted = false")
    List<Member> findProtectorsByDependentAndDeletedIsFalse(Member dependent);


    // 피보호자와 보호자 id로 관계 조회
    @Query("SELECT d FROM Dependency d WHERE d.dependent.id = :dependentId AND d.protector.id = :protectorId AND d.deleted = false")
    Optional<Dependency> findByDependentIdAndProtectorIdAndDeletedIsFalse(@Param("dependentId") Long dependentId, @Param("protectorId") Long protectorId);

    // 회원 id로 모든 관계 조회
    @Query("SELECT d FROM Dependency d WHERE (d.protector.id = :memberId OR d.dependent.id = :memberId) AND d.deleted = false")
    List<Dependency> findAllByProtectorIdAndDeletedIsFalseOrDependentIdAndDeletedIsFalse(Long memberId);
}
