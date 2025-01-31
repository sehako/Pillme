package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.management.domain.Management;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementRepository extends JpaRepository<Management, Long> {
}
