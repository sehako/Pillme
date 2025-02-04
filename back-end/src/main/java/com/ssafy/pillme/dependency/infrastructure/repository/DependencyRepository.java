package com.ssafy.pillme.dependency.infrastructure.repository;

import com.ssafy.pillme.dependency.domain.entity.Dependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DependencyRepository extends JpaRepository<Dependency, Long> {
    // 회원의 삭제되지 않은 모든 관계 조회
    List<Dependency> findAllByProtectorIdAndDeletedIsFalse(Long protectorId);
}
