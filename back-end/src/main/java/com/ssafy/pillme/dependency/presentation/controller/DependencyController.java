package com.ssafy.pillme.dependency.presentation.controller;

import com.ssafy.pillme.auth.annotation.Auth;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.dependency.application.response.DependentListResponse;
import com.ssafy.pillme.dependency.application.response.RelationShipListResponse;
import com.ssafy.pillme.dependency.application.service.DependencyService;
import com.ssafy.pillme.dependency.presentation.request.*;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dependency")
@RequiredArgsConstructor
public class DependencyController {
    private final DependencyService dependencyService;

    // 보호자가 피보호자 등록 요청
    @PostMapping
    public ResponseEntity<JSONResponse<Void>> requestDependency(@RequestBody DependentPhoneRequest request, @Auth Member loginMember) {
        dependencyService.requestDependency(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 피보호자가 보호자 등록 요청 수락
    @PostMapping("/accept")
    public ResponseEntity<JSONResponse<Void>> acceptDependency(@RequestBody DependencyAcceptRequest request, @Auth Member loginMember) {
        dependencyService.acceptDependency(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 피보호자가 보호자 등록 요청 거절
    @PostMapping("/reject")
    public ResponseEntity<JSONResponse<Void>> rejectDependency(@RequestBody DependencyRejectRequest request, @Auth Member loginMember) {
        dependencyService.rejectDependency(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 로컬 회원(피보호자) 등록
    @PostMapping("/local-member")
    public ResponseEntity<JSONResponse<Void>> createLocalMember(@RequestBody LocalMemberRequest request, @Auth Member loginMember) {
        dependencyService.createLocalMemberWithDependency(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 피보호자 관리 목록
    @GetMapping("/dependents")
    public ResponseEntity<JSONResponse<List<DependentListResponse>>> getDependents(@Auth Member loginMember) {
        return ResponseEntity.ok(JSONResponse.onSuccess(dependencyService.getDependents(loginMember)));
    }

    /* 가족 관계 삭제 요청 - 보호자와 피보호자 모두 삭제 요청가능
     * dependencyId(가족 관계 ID)를 받아서 삭제
     * 현재 로그인한 사용자가 삭제를 요청하는 입장.
     * 삭제 요청을 보내는 회원이 sender, 삭제 요청을 받는 회원이 receiver
     * */
    @PostMapping("/delete/{dependencyId}")
    public ResponseEntity<JSONResponse<Void>> deleteRequestDependency(@PathVariable Long dependencyId, @Auth Member loginMember) {
        dependencyService.deleteRequestDependency(dependencyId, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /*
     * 가족 관계 삭제 요청 수락 - 삭제 요청을 받은 회원이 삭제 요청을 수락하는 경우
     * 메시지에 존재하는 senderId를 통해 삭제 요청을 보낸 회원을 찾아서 삭제 요청을 수락
     * */
    @PostMapping("/delete/accept")
    public ResponseEntity<JSONResponse<Void>> acceptDeleteDependency(@RequestBody AcceptDependencyDeletionRequest request, @Auth Member loginMember) {
        dependencyService.acceptDeleteDependency(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    /*
     * 가족 관계 삭제 요청 거절 - 삭제 요청을 받은 회원이 삭제 요청을 거절하는 경우
     * 메시지에 존재하는 senderId를 통해 삭제 요청을 보낸 회원을 찾아서 삭제 요청을 거절
     */
    @PostMapping("/delete/reject")
    public ResponseEntity<JSONResponse<Void>> rejectDeleteDependency(@RequestBody RejectDependencyDeletionRequest request, @Auth Member loginMember) {
        dependencyService.rejectDeleteDependency(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 보호자가 피보호자에게 약 복용 알림 전송 기능
    @PostMapping("/medicine")
    public ResponseEntity<JSONResponse<Void>> sendMedicineNotification(@RequestBody SendMedicineNotificationRequest request, @Auth Member loginMember) {
        dependencyService.sendMedicineNotification(request, loginMember);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 피보호자와 자신의 보호자 목록 조회
    @GetMapping("/relationships")
    public ResponseEntity<JSONResponse<RelationShipListResponse>> getRelationships(@Auth Member loginMember) {
        return ResponseEntity.ok(JSONResponse.onSuccess(dependencyService.getRelationships(loginMember)));
    }
}
