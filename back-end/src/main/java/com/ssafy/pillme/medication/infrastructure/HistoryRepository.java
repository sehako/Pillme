package com.ssafy.pillme.medication.infrastructure;

import com.ssafy.pillme.medication.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
}
