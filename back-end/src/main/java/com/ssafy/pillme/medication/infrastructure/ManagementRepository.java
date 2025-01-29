package com.ssafy.pillme.medication.infrastructure;

import com.ssafy.pillme.medication.domain.Management;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementRepository extends JpaRepository<Management, Long> {
}
