package com.ssafy.pillme.history.presentation;

import com.ssafy.pillme.auth.annotation.Auth;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.history.application.HistoryService;
import com.ssafy.pillme.history.application.response.HistorySearchResponse;
import com.ssafy.pillme.history.domain.dto.HistorySearchFilter;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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
            @DateTimeFormat(iso = ISO.DATE, pattern = "yyyy-MM-dd") final LocalDate startDate,
            @RequestParam(value = "end-date")
            @DateTimeFormat(iso = ISO.DATE, pattern = "yyyy-MM-dd") final LocalDate endDate,
            @RequestParam(value = "target") final Long memberId,
            @RequestParam(value = "hospital", required = false) final String hospital,
            @RequestParam(value = "diseaseName", required = false) final String diseaseName
    ) {
        return ResponseEntity.ok(
                JSONResponse.onSuccess(
                        historyService.selectHistoryWithFilter(
                                HistorySearchFilter.of(startDate, endDate, hospital, diseaseName, memberId))
                )
        );
    }

    @GetMapping("/{info-id}")
    public void searchHistory(
            @PathVariable("info-id") final Long informationId
    ) {
        historyService.selectHistoryByInformationId(informationId);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<JSONResponse<Void>> deleteHistory(
            @PathVariable(value = "id") final Long historyId,
            @Auth final Member member
    ) {
        historyService.deleteHistory(historyId, member);
        return ResponseEntity.noContent().build();
    }
}
