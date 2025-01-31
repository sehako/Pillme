package com.ssafy.pillme.search.infrastructure;

import com.ssafy.pillme.search.domain.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
}
