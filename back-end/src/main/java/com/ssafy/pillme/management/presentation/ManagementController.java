package com.ssafy.pillme.management.presentation;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.management.application.ManagementService;
import com.ssafy.pillme.management.presentation.request.MedicationRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/medication")
@RequiredArgsConstructor
public class ManagementController {
    private final ManagementService medicationService;

    @PostMapping
    public ResponseEntity<JSONResponse<Void>> register(@RequestBody MedicationRegisterRequest request) {
        medicationService.saveTakingInformation(request);
        return ResponseEntity.ok().body(JSONResponse.onSuccess());
    }

    @GetMapping("/{info-id}")
    public void currentTakingDetail(@PathVariable("info-id") Long infoId) {

    }

    @GetMapping
    public void currentTakingAll() {

    }
}
