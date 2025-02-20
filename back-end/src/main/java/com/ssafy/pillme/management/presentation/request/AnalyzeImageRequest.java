package com.ssafy.pillme.management.presentation.request;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record AnalyzeImageRequest(
        Long readerId,
        String hospital,
        String diseaseName,
        @DateTimeFormat(
                iso = DateTimeFormat.ISO.DATE,
                pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @DateTimeFormat(
                iso = DateTimeFormat.ISO.DATE,
                pattern = "yyyy-MM-dd")
        LocalDate endDate
) {
}
