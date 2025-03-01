package com.americanstartup.pillme.search.infrastructure;

import com.americanstartup.pillme.search.domain.MedicationDocument;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class MedicationElasticRepositoryTest {

    @Autowired
    MedicationElasticRepository medicationElasticRepository;

    @Test
    void getMedication() {
        Pageable pageable = PageRequest.of(0, 10);
        List<MedicationDocument> medication = medicationElasticRepository.findByName("ㅌ", pageable);

        for (MedicationDocument elasticMedication : medication) {
            System.out.println(elasticMedication.toString());
        }
    }

}