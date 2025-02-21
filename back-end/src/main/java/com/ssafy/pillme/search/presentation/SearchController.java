package com.ssafy.pillme.search.presentation;

import com.ssafy.pillme.global.code.SuccessCode;
import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.search.application.SearchService;
import com.ssafy.pillme.search.application.response.MedicationSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<JSONResponse<List<MedicationSearchResponse>>> getMedication(
            @RequestParam("keyword") String keyword
    ) {
        return ResponseEntity.ok(
                JSONResponse.of(
                        SuccessCode.MEDICATION_SEARCH_SUCCESS, searchService.searchMedication(keyword)
                )
        );
    }
}
