package com.ssafy.pillme.search.presentation;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.management.application.ManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final ManagementService medicationService;

//    @PostMapping
//    public void register(@RequestBody TakingInformationRegisterRequest request) {
//        medicationService.saveTakingInformation(request);
//    }

    @GetMapping
    public void currentTakingAll() {

    }

    @GetMapping("/{info-id}")
    public ResponseEntity<JSONResponse<Long>> currentTakingDetail(@PathVariable("info-id") Long infoId) {
        return ResponseEntity.ok(JSONResponse.onSuccess(infoId));
    }
}
