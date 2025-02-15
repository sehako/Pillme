package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.management.domain.Information;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationRepository extends JpaRepository<Information, Long> {

    @Query("SELECT i FROM Information i "
            + "JOIN FETCH i.writer "
            + "JOIN FETCH i.reader "
            + "WHERE i.id = :id AND NOT i.deleted")
    Optional<Information> findByIdMemberFetchJoin(Long id);

    Optional<Information> findByIdAndReaderIdAndDeletedIsFalse(Long id, Long readerId);

    @Query("SELECT i FROM Information i "
            + "JOIN FETCH i.reader "
            + "JOIN FETCH i.writer "
            + "WHERE i.reader = :readerId "
            + "AND CURRENT_DATE BETWEEN i.startDate AND i.endDate AND NOT i.deleted")
    List<Information> findCurrentDateAndReaderId(Long readerId);
}
