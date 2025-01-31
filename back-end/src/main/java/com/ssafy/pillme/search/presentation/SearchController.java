package com.ssafy.pillme.search.presentation;

import com.ssafy.pillme.management.application.ManagementService;
import com.ssafy.pillme.management.presentation.request.MedicationRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final ManagementService medicationService;

    @PostMapping
    public void register(@RequestBody MedicationRegisterRequest request) {
        medicationService.saveTakingInformation(request);
    }

    @GetMapping
    public void currentTakingAll() {

    }

    @GetMapping("/{info-id}")
    public void currentTakingDetail(@PathVariable("info-id") Long infoId) {

    }
}
