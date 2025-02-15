package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.management.domain.Management;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementRepository extends JpaRepository<Management, Long> {
    @EntityGraph(attributePaths = {"information"})
    @Query("SELECT m FROM Management m "
            + "WHERE m.id IN :managementList "
            + "AND m.information.id = :informationId AND NOT m.deleted")
    List<Management> findManagementList(List<Long> managementList, Long informationId);

    @Query("SELECT m FROM Management m JOIN FETCH m.information i "
            + "WHERE m.id = :id")
    Optional<Management> findByIdFetch(Long id);

    @Query("SELECT m FROM Management m "
            + "JOIN FETCH m.information i "
            + "JOIN FETCH i.reader r "
            + "WHERE r.id = :readerId AND CURRENT_DATE BETWEEN i.startDate AND i.endDate AND NOT m.deleted")
    List<Management> findManagementsByInformationDateAndReaderIdFetch(Long readerId);

    @Query("SELECT m FROM Management m "
            + "JOIN FETCH m.information i "
            + "JOIN FETCH i.reader r "
            + "WHERE r.id = :readerId AND i.id = :infoId AND NOT m.deleted")
    List<Management> findManagementsByInformationIdAndReaderIdFetch(Long infoId, Long readerId);

    List<Management> findByInformationIdAndInformationWriterIdAndDeletedIsFalse(Long informationId, Long writerId);

    List<Management> findByInformationIdAndInformationReaderIdAndDeletedIsFalse(Long informationId, Long readerId);

    @Query("SELECT m FROM Management m "
            + "JOIN FETCH m.information i "
            + "JOIN FETCH i.reader "
            + "JOIN FETCH i.writer "
            + "WHERE CURRENT_DATE - 1 DAY BETWEEN i.startDate AND i.endDate")
    List<Management> findYesterdayManagements();
}
