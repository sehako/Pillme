package com.ssafy.pillme.search.application;

import com.ssafy.pillme.search.application.response.MedicationSearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchServiceTest {
    @Autowired
    SearchService searchService;

    @Test
    void searchTest() {
        for (MedicationSearchResponse result : searchService.searchMedication("타이")) {
            System.out.println(result);
        }
    }
}