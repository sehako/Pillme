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
            + "WHERE i.id = :id AND NOT i.deleted AND NOT i.requested")
    Optional<Information> findByIdMemberFetchJoin(Long id);

    Optional<Information> findByIdAndReaderIdAndDeletedIsFalse(Long id, Long readerId);

    @Query("SELECT i FROM Information i "
            + "JOIN FETCH i.reader r "
            + "WHERE r.id = :memberId AND i.requested AND NOT i.deleted")
    Optional<Information> findUnAcceptedInformation(Long memberId);

    @Query("SELECT i FROM Information i "
            + "JOIN FETCH i.reader r "
            + "JOIN FETCH i.writer w "
            + "WHERE r.id = :readerId "
            + "AND CURRENT_DATE BETWEEN i.startDate AND i.endDate AND NOT i.deleted AND NOT i.requested")
    List<Information> findCurrentDateAndReaderId(Long readerId);

    @Query("SELECT i FROM Information i "
            + "JOIN FETCH i.reader r "
            + "JOIN FETCH i.writer w "
            + "WHERE r.id = :readerId "
            + "AND NOT i.deleted AND NOT i.requested "
            + "AND (FUNCTION('DATE_FORMAT', i.startDate, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE , '%Y-%m') "
            + "OR FUNCTION('DATE_FORMAT', i.endDate, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m')) "
            + "ORDER BY i.startDate ASC")
    List<Information> findAllByDate(Long readerId);
}
