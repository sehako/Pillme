package com.ssafy.pillme.history.infrastructure;

import com.ssafy.pillme.history.domain.History;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("SELECT h FROM History h "
            + "JOIN FETCH h.management "
            + "JOIN FETCH h.information i "
            + "JOIN FETCH h.member "
            + "WHERE h.id = :id")
    Optional<History> findByIdFetch(final Long id);

    @Query("SELECT h FROM History h "
            + "JOIN FETCH h.management "
            + "JOIN FETCH h.information i "
            + "JOIN FETCH h.information.reader r "
            + "WHERE r.id = :readerId")
    List<History> findByReaderIdFetch(final Long readerId);

    List<History> findByIdInAndDeletedIsFalse(final List<Long> ids);

    @Query("SELECT h FROM History h "
            + "JOIN FETCH h.management "
            + "JOIN FETCH h.information i "
            + "WHERE i.id = :informationId AND i.reader.id = :target")
    List<History> findHistoryByInformationId(final Long informationId, final Long target);
}
