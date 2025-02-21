package com.ssafy.pillme.admin.presentation.controller;

import com.ssafy.pillme.admin.application.response.SinginMemberResponse;
import com.ssafy.pillme.admin.application.service.AdminService;
import com.ssafy.pillme.admin.presentation.request.MemberUpdateRequest;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    /**
     * 회원 통계 정보 조회
     */
    @GetMapping("/members/stats")
    public ResponseEntity<JSONResponse<Map<String, Long>>> getMemberStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", adminService.countTotalUser());
        stats.put("active", adminService.countActiveUser());
        stats.put("deleted", adminService.countDeletedUser());
        return ResponseEntity.ok(JSONResponse.onSuccess(stats));
    }

    /**
     * 회원 목록 페이징 조회
     */
    @GetMapping("/members")
    public ResponseEntity<JSONResponse<Page<SinginMemberResponse>>> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SinginMemberResponse> members = adminService.listUpUser(page, size);
        return ResponseEntity.ok(JSONResponse.onSuccess(members));
    }

    @PatchMapping("/members/{id}/status")
    public ResponseEntity<JSONResponse<Void>> updateMemberStatus(
            @PathVariable Long id,
            @RequestParam boolean isDeleted) {
        adminService.updateMemberStatus(id, isDeleted);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<JSONResponse<Void>> updateMember(
            @PathVariable Long id,
            @RequestBody MemberUpdateRequest request) {
        adminService.updateMemberInfo(id, request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    @GetMapping("/members/search")
    public ResponseEntity<JSONResponse<Page<SinginMemberResponse>>> searchMembers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SinginMemberResponse> members = adminService.searchMembers(keyword, pageable);
        return ResponseEntity.ok(JSONResponse.onSuccess(members));
    }
}
