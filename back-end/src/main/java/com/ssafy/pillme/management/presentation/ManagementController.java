package com.ssafy.pillme.management.presentation;

import static com.ssafy.pillme.global.code.SuccessCode.INFORMATION_ADD_REQUEST_SUCCESS;
import static com.ssafy.pillme.global.code.SuccessCode.INFORMATION_ADD_SUCCESS;
import static com.ssafy.pillme.global.code.SuccessCode.INFORMATION_DELETE_REQUEST_REJECT_SUCCESS;
import static com.ssafy.pillme.global.code.SuccessCode.INFORMATION_DELETE_REQUEST_SUCCESS;
import static com.ssafy.pillme.global.code.SuccessCode.INFORMATION_DELETE_SUCCESS;
import static com.ssafy.pillme.global.code.SuccessCode.INFORMATION_SAVE_SUCCESS;
import static com.ssafy.pillme.global.code.SuccessCode.MANAGEMENT_CHANGE_SUCCESS;
import static com.ssafy.pillme.global.code.SuccessCode.MEDICATION_CHECK_SUCCESS;

import com.ssafy.pillme.auth.annotation.Auth;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.global.response.JSONResponse;
import com.ssafy.pillme.management.application.ManagementService;
import com.ssafy.pillme.management.application.response.CurrentTakingResponse;
import com.ssafy.pillme.management.application.response.TakingDetailResponse;
import com.ssafy.pillme.management.application.response.TakingPrescriptionResponse;
import com.ssafy.pillme.management.domain.Information;
import com.ssafy.pillme.management.presentation.request.AddTakingInformationRequest;
import com.ssafy.pillme.management.presentation.request.AllTakingCheckRequest;
import com.ssafy.pillme.management.presentation.request.AnalyzeImageRequest;
import com.ssafy.pillme.management.presentation.request.ChangeTakingInformationRequest;
import com.ssafy.pillme.management.presentation.request.CheckCurrentTakingRequest;
import com.ssafy.pillme.management.presentation.request.DeleteManagementRequest;
import com.ssafy.pillme.management.presentation.request.SingleTakingCheckRequest;
import com.ssafy.pillme.management.presentation.request.TakingInformationRegisterRequest;
import java.net.URI;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/v1/management")
@RequiredArgsConstructor
public class ManagementController {
    private final ManagementService managementService;

    @PostMapping("/analyze")
    public ResponseEntity<JSONResponse<Void>> analyzePrescription(
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "detail") AnalyzeImageRequest request,
            @Auth Member member
    ) {
        managementService.requestToFastApi(image, request, member);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<JSONResponse<Void>> register(
            @RequestBody final TakingInformationRegisterRequest request,
            @Auth Member writer
    ) {
        Information information = managementService.saveTakingInformation(request, writer);
        return ResponseEntity.created(URI.create("/api/v1/management/" + information.getId()))
                .body(JSONResponse.of(
                        information.isRequested() ? INFORMATION_ADD_REQUEST_SUCCESS : INFORMATION_SAVE_SUCCESS));
    }

    @PostMapping("/accept/{writer}")
    public ResponseEntity<JSONResponse<Void>> acceptDependencyInformation(
            @PathVariable(value = "writer") Long writer,
            @Auth Member reader
    ) {
        Information information = managementService.acceptInformationRegisterRequest(writer, reader);
        return ResponseEntity
                .created(URI.create("/api/v1/management/" + information.getId()))
                .body(JSONResponse.of(INFORMATION_SAVE_SUCCESS));
    }

    @DeleteMapping("/reject/{writer}")
    public ResponseEntity<Void> rejectDependencyInformation(
            @PathVariable(value = "writer") Long writer,
            @Auth Member reader
    ) {
        managementService.rejectInformationRegisterRequest(writer, reader);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{info-id}")
    public ResponseEntity<JSONResponse<Void>> addTakingInformation(
            @PathVariable(value = "info-id") Long infoId,
            @RequestBody AddTakingInformationRequest request,
            @Auth Member member
    ) {
        Information information = managementService.addTakingInformation(infoId, request, member);
        return ResponseEntity.created(URI.create("/api/v1/management/" + information.getId()))
                .body(JSONResponse.of(INFORMATION_ADD_SUCCESS));
    }

    @GetMapping
    public ResponseEntity<JSONResponse<List<CurrentTakingResponse>>> getCurrentTakingAll(
            @RequestParam(value = "target") Long memberId
    ) {
        return ResponseEntity.ok(
                JSONResponse.onSuccess(
                        managementService.selectManagementByDate(memberId)
                )
        );
    }

    @GetMapping("/prescription")
    public ResponseEntity<JSONResponse<List<TakingPrescriptionResponse>>> getTakingPrescription(
            @RequestParam("target") final Long targetId,
            @RequestParam("date")
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE,
                    pattern = "yyyy-MM-dd") final LocalDate date
    ) {
        return ResponseEntity.ok(
                JSONResponse.onSuccess(managementService.selectTakingPrescription(targetId, date))
        );
    }

    @GetMapping("/{info-id}")
    public ResponseEntity<JSONResponse<TakingDetailResponse>> takingDetail(
            @PathVariable(value = "info-id") final Long infoId,
            @RequestParam(value = "reader") final Long readerId
    ) {
        return ResponseEntity.ok(
                JSONResponse.onSuccess(managementService.selectInformation(infoId, readerId))
        );
    }

    @PutMapping("/{info-id}")
    public ResponseEntity<JSONResponse<TakingDetailResponse>> changePillTakingInformation(
            @PathVariable(value = "info-id") final Long infoId,
            @RequestBody final ChangeTakingInformationRequest request,
            @Auth Member member
    ) {
        return ResponseEntity.ok(
                JSONResponse.of(
                        MANAGEMENT_CHANGE_SUCCESS,
                        managementService.changeTakingInformation(infoId, request, member)
                )
        );
    }

    @PatchMapping("/check-taking")
    public ResponseEntity<JSONResponse<Void>> checkSingleMedication(
            @RequestBody final SingleTakingCheckRequest request,
            @Auth Member member
    ) {
        managementService.checkSingleMedicationTaking(request, member);
        return ResponseEntity.ok(
                JSONResponse.of(MEDICATION_CHECK_SUCCESS)
        );
    }

    @PatchMapping("/check-taking/{info-id}")
    public ResponseEntity<JSONResponse<Void>> checkAllMedication(
            @PathVariable(value = "info-id") final Long infoId,
            @RequestBody final AllTakingCheckRequest request,
            @Auth Member member
    ) {
        managementService.checkAllMedicationTaking(infoId, request, member);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    @PatchMapping("/check-current-taking/all")
    public ResponseEntity<JSONResponse<Void>> checkCurrentTakingAll(
            @RequestBody CheckCurrentTakingRequest request,
            @Auth Member member
    ) {
        managementService.checkCurrentTakingAll(request, member);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    @DeleteMapping("/{info-id}")
    public ResponseEntity<JSONResponse<Void>> deleteManagement(
            @PathVariable(value = "info-id") final Long infoId,
            @RequestBody final DeleteManagementRequest request,
            @Auth final Member member
    ) {
        boolean deleteMyself = managementService.deleteManagement(infoId, request, member);
        return ResponseEntity.ok(
                JSONResponse.of(deleteMyself ? INFORMATION_DELETE_SUCCESS : INFORMATION_DELETE_REQUEST_SUCCESS)
        );
    }

    @DeleteMapping("/delete-accept/{reader-id}")
    public ResponseEntity<Void> deleteAccept(
            @PathVariable(value = "reader-id") final Long readerId,
            @Auth final Member member
    ) {
        managementService.acceptDependentDeleteRequest(readerId, member);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete-reject/{reader-id}")
    public ResponseEntity<JSONResponse<Void>> deleteReject(
            @PathVariable(value = "reader-id") final Long readerId,
            @Auth final Member member
    ) {
        managementService.rejectDependentDeleteRequest(readerId, member);
        return ResponseEntity.ok(
                JSONResponse.of(INFORMATION_DELETE_REQUEST_REJECT_SUCCESS)
        );
    }
}
