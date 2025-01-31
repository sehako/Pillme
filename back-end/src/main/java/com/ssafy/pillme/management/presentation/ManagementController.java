package com.ssafy.pillme.management.presentation;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.management.application.ManagementService;
import com.ssafy.pillme.management.application.response.TakingDetailResponse;
import com.ssafy.pillme.management.presentation.request.MedicationRegisterRequest;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<JSONResponse<TakingDetailResponse>> takingDetail(@PathVariable("info-id") Long infoId) {
        return ResponseEntity.ok(
                JSONResponse.onSuccess(medicationService.findByInformationId(infoId))
        );
    }

    @GetMapping
    public void currentTakingAll(
            @RequestParam("date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate localDate
    ) {
        medicationService.findManagementByDate(localDate);
    }
}
