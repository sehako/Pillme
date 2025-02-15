package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.management.domain.Management;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementRepository extends JpaRepository<Management, Long>, ManagementRepositoryCustom {
    Optional<Management> findByIdAndInformationId(Long managementId, Long informationId);

    @Query("SELECT m FROM Management m JOIN FETCH Information i WHERE i.id = :informationId")
    Optional<Management> findByIdFetch(Long informationId);
}
