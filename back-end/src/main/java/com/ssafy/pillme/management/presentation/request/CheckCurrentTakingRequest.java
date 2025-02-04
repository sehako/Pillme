package com.ssafy.pillme.management.presentation.request;

import com.ssafy.pillme.management.domain.type.TakingType;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record CheckCurrentTakingRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        LocalDate date,
        TakingType time
) {
}
