package com.ssafy.pillme.management.application;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ManagementServiceTest {
    @Test
    void dateTest() {
        LocalDate today = LocalDate.now();
        System.out.println("today = " + today);
    }
}