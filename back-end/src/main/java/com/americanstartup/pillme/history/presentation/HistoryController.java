package com.americanstartup.pillme.history.presentation;

import com.americanstartup.pillme.auth.annotation.Auth;
import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.global.response.JSONResponse;
import com.americanstartup.pillme.history.application.HistoryService;
import com.americanstartup.pillme.history.application.response.HistoryDetailResponse;
import com.americanstartup.pillme.history.application.response.HistorySearchResponse;
import com.americanstartup.pillme.history.presentation.request.PatchHistoryRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<JSONResponse<List<HistorySearchResponse>>> searchHistory(
            @RequestParam(value = "target") final Long memberId
    ) {
        return ResponseEntity.ok(
                JSONResponse.onSuccess(
                        historyService.selectHistoryTarget(memberId)
                ));
    }

    @GetMapping("/{info-id}")
    public ResponseEntity<JSONResponse<List<HistoryDetailResponse>>> searchHistory(
            @PathVariable(value = "info-id") final Long informationId,
            @RequestParam(value = "target") final Long target
    ) {
        return ResponseEntity.ok(JSONResponse.onSuccess(
                historyService.selectHistoryByInformationId(informationId, target)
        ));
    }

    @PatchMapping("/modify")
    public ResponseEntity<JSONResponse<HistoryDetailResponse>> modifyHistory(
            @RequestBody final PatchHistoryRequest request,
            @Auth final Member member
    ) {
        historyService.patchHistories(request, member);
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JSONResponse<Void>> deleteHistory(
            @PathVariable(value = "id") final Long historyId
    ) {
        historyService.deleteHistory(historyId);
        return ResponseEntity.noContent().build();
    }
}
