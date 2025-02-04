package com.ssafy.pillme.dependency.presentation.controller;

import com.ssafy.pillme.dependency.application.service.DependencyService;
import com.ssafy.pillme.dependency.presentation.request.DependencyAcceptRequest;
import com.ssafy.pillme.dependency.presentation.request.DependencyRejectRequest;
import com.ssafy.pillme.dependency.presentation.request.DependentPhoneRequest;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dependency")
@RequiredArgsConstructor
public class DependencyController {
    private final DependencyService dependencyService;

    // 보호자가 피보호자 등록 요청
    @PostMapping
    public ResponseEntity<JSONResponse<Void>> requestDependency(@RequestBody DependentPhoneRequest request) {
        dependencyService.requestDependency(request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 피보호자가 보호자 등록 요청 수락
    @PostMapping("/accept")
    public ResponseEntity<JSONResponse<Void>> acceptDependency(@RequestBody DependencyAcceptRequest request) {
        dependencyService.acceptDependency(request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }

    // 피보호자가 보호자 등록 요청 거절
    @PostMapping("/reject")
    public ResponseEntity<JSONResponse<Void>> rejectDependency(@RequestBody DependencyRejectRequest request) {
        dependencyService.rejectDependency(request);
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
