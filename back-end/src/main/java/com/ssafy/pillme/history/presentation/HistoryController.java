package com.ssafy.pillme.history.presentation;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.history.application.HistoryService;
import com.ssafy.pillme.history.application.response.HistorySearchResponse;
import com.ssafy.pillme.history.domain.dto.HistorySearchFilter;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<JSONResponse<List<HistorySearchResponse>>> searchHistory(
            @RequestParam(value = "start-date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,
            @RequestParam(value = "end-date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate,
            @RequestParam(value = "hospital", required = false)
            String hospital,
            @RequestParam(value = "diseaseName", required = false)
            String diseaseName,
            @RequestParam(value = "target")
            Long memberId
    ) {
        return ResponseEntity.ok(
                JSONResponse.onSuccess(
                        historyService.selectHistoryWithFilter(
                                HistorySearchFilter.of(startDate, endDate, hospital, diseaseName, memberId))
                )
        );
    }

    @GetMapping
    public void searchDetailHistory(
            @RequestParam(value = "date")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {

    }

    @DeleteMapping("{id}")
    public ResponseEntity<JSONResponse<Void>> deleteHistory(
            @PathVariable(value = "id") Long id
    ) {
        historyService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
