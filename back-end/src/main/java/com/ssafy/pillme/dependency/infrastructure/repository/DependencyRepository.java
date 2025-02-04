package com.ssafy.pillme.dependency.infrastructure.repository;

import com.ssafy.pillme.dependency.domain.entity.Dependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DependencyRepository extends JpaRepository<Dependency, Long> {
}
