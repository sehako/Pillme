package com.ssafy.pillme.medication.infrastructure;

import com.ssafy.pillme.medication.domain.Information;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationRepository extends JpaRepository<Information, Long> {
}
