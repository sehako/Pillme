package com.ssafy.pillme.search.infrastructure;

import com.ssafy.pillme.search.domain.elasticsearch.MedicationElastic;
import com.ssafy.pillme.search.infrastructure.elasticsearch.MedicationElasticRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MedicationRepositoryTest {
    @Autowired
    MedicationElasticRepository medicationRepository;

    @Test
    void getMedication() {
        MedicationElastic medication = medicationRepository.findById("KD1t1ZQBx7ftf5b5_H6G")
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        System.out.println(medication.toString());
    }

    @Test
    void getMedicationByName() {
        MedicationElastic medicationElastic = medicationRepository.findByName("치오단정(요오드화칼륨)")
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        System.out.println(medicationElastic.toString());
    }
}