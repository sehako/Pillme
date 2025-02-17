package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.management.domain.Management;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementRepository extends JpaRepository<Management, Long> {
    @Query("SELECT m FROM Management m "
            + "JOIN FETCH m.information i "
            + "WHERE m.id IN :managementList "
            + "AND m.information.id = :informationId AND NOT m.deleted AND NOT i.requested")
    List<Management> findManagementList(List<Long> managementList, Long informationId);

    @Query("SELECT m FROM Management m "
            + "JOIN FETCH m.information i "
            + "JOIN FETCH i.reader r "
            + "WHERE r.id = :readerId AND CURRENT_DATE BETWEEN i.startDate AND i.endDate AND NOT m.deleted AND NOT i.requested")
    List<Management> findManagementsByInformationDateAndReaderIdFetch(Long readerId);

    @Query("SELECT m FROM Management m "
            + "JOIN FETCH m.information i "
            + "JOIN FETCH i.reader r "
            + "WHERE r.id = :readerId AND i.id = :infoId AND NOT m.deleted AND NOT i.requested")
    List<Management> findManagementsByInformationIdAndReaderIdFetch(Long infoId, Long readerId);

    @Query("SELECT m FROM Management m "
            + "JOIN FETCH m.information i "
            + "JOIN FETCH i.writer w "
            + "WHERE w.id = :writerId AND i.id = :informationId AND NOT i.deleted AND NOT i.requested")
    List<Management> findByInformationIdAndInformationWriterIdAndDeletedIsFalse(Long informationId, Long writerId);

    @Query("SELECT m FROM Management m "
            + "JOIN FETCH m.information i "
            + "JOIN FETCH i.reader r "
            + "WHERE r.id = :readerId AND i.id = :informationId AND NOT i.deleted AND NOT i.requested")
    List<Management> findByInformationIdAndInformationReaderIdAndDeletedIsFalse(Long informationId, Long readerId);

    @Query("SELECT m FROM Management m "
            + "JOIN FETCH m.information i "
            + "JOIN FETCH i.reader "
            + "JOIN FETCH i.writer "
            + "WHERE CURRENT_DATE - 1 DAY BETWEEN i.startDate AND i.endDate AND NOT i.requested")
    List<Management> findYesterdayManagements();
}
