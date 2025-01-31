package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.management.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
}
