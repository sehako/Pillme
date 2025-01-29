package com.ssafy.pillme.medication.infrastructure;

import com.ssafy.pillme.medication.domain.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
}
