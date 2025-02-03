package com.ssafy.pillme.management.presentation;

import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.management.application.ManagementService;
import com.ssafy.pillme.management.application.response.PrescriptionResponse;
import com.ssafy.pillme.management.application.response.TakingDetailResponse;
import com.ssafy.pillme.management.presentation.request.AllTakingCheckRequest;
import com.ssafy.pillme.management.presentation.request.ChangeTakingInformationRequest;
import com.ssafy.pillme.management.presentation.request.DeleteManagementRequest;
import com.ssafy.pillme.management.presentation.request.MedicationRegisterRequest;
import com.ssafy.pillme.management.presentation.request.SingleTakingCheckRequest;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/management")
@RequiredArgsConstructor
public class ManagementController {
    private final ManagementService managementService;

    @PostMapping
    public ResponseEntity<JSONResponse<Void>> register(@RequestBody final MedicationRegisterRequest request) {
        managementService.saveTakingInformation(request);
        return ResponseEntity.ok().body(JSONResponse.onSuccess());
    }

    @GetMapping("/{info-id}")
    public ResponseEntity<JSONResponse<TakingDetailResponse>> takingDetail(@PathVariable("info-id") final Long infoId) {
        return ResponseEntity.ok(
                JSONResponse.onSuccess(managementService.findByInformationId(infoId))
        );
    }

    @GetMapping
    public ResponseEntity<JSONResponse<List<PrescriptionResponse>>> currentTakingAll(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate localDate
    ) {
        return ResponseEntity.ok(
                JSONResponse.onSuccess(managementService.findManagementByDate(localDate))
        );
    }

    @PutMapping("/{info-id}")
    public ResponseEntity<JSONResponse<Void>> changePillTakingInfoById(
            @PathVariable(value = "info-id") final Long infoId,
            @RequestBody final ChangeTakingInformationRequest request) {
        managementService.changeTakingInformation(infoId, request);
        return ResponseEntity.ok().body(JSONResponse.onSuccess());
    }

    @PatchMapping("/check-taking/{info-id}")
    public ResponseEntity<JSONResponse<Void>> checkSingleMedication(
            @PathVariable(value = "info-id") final Long infoId,
            @RequestBody final SingleTakingCheckRequest request
    ) {
        managementService.checkSingleMedicationTaking(infoId, request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    @PatchMapping("/check-taking-all/{info-id}")
    public ResponseEntity<JSONResponse<Void>> checkAllMedication(
            @PathVariable(value = "info-id") final Long infoId,
            @RequestBody final AllTakingCheckRequest request
    ) {
        managementService.checkAllMedicationTaking(infoId, request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    @DeleteMapping("/{info-id}")
    public ResponseEntity<Void> deleteManagement(
            @PathVariable(value = "info-id") final Long infoId,
            @RequestBody final DeleteManagementRequest request
    ) {
        managementService.deleteManagement(infoId, request);

        return ResponseEntity.noContent().build();
    }
}
