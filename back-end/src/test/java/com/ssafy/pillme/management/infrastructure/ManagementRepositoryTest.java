package com.ssafy.pillme.management.infrastructure;

import com.ssafy.pillme.management.domain.Management;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ManagementRepositoryTest {
    @Autowired
    ManagementRepository managementRepository;

    @Test
    void inQueryTest() {
        List<Long> ids = new ArrayList<>();

        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        ids.add(4L);

        List<Management> managementList = managementRepository.findManagementList(ids, 4L);

        for (Management management : managementList) {
            System.out.println(management.getMedicationName());
        }
    }


    @Test
    void getDeleteTarget() {
        List<Management> managements = managementRepository.findByInformationIdAndInformationReaderIdAndDeletedIsFalse(
                4L, 1L);

        for (Management management : managements) {
            System.out.println(management.getMedicationName());
        }
    }

    @Test
    void 어제복약내역테스트() {
        List<Management> managements = managementRepository.findYesterdayManagements();

        for (Management management : managements) {
            System.out.println(management.getMedicationName());
        }
    }
}