package com.ssafy.pillme.history.infrastructure;

import com.ssafy.pillme.history.domain.History;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {
    Optional<History> findByIdAndDeletedIsFalse(Long id);
}
