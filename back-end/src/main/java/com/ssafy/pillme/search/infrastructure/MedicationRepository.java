package com.ssafy.pillme.search.infrastructure;

import com.ssafy.pillme.search.application.response.MedicationSearchResponse;
import com.ssafy.pillme.search.domain.Medication;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    @Query("SELECT new com.ssafy.pillme.search.application.response.MedicationSearchResponse(m.name, m.company, m.image) "
            + "FROM Medication m WHERE m.name LIKE CONCAT(:keyword, '%') ORDER BY LENGTH(m.name) ASC")
    List<MedicationSearchResponse> findByKeyword(String keyword, Pageable pageable);
}
