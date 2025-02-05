package com.ssafy.pillme.dependency.infrastructure.repository;

import com.ssafy.pillme.dependency.domain.entity.Dependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    Optional<Dependency> findByMemberIdsAndDeletedIsFalse(Long firstMemberId, Long secondMemberId);
}
