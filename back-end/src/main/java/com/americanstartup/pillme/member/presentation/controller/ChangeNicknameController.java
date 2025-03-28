package com.americanstartup.pillme.member.presentation.controller;

import com.americanstartup.pillme.global.response.JSONResponse;
import com.americanstartup.pillme.member.application.service.ChangeNicknameService;
import com.americanstartup.pillme.member.domain.vo.NicknameValidationResult;
import com.americanstartup.pillme.member.presentation.request.ChangeNicknameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class ChangeNicknameController {
    private final ChangeNicknameService changeNicknameService;

    // 닉네임 중복 검증
    @GetMapping("/check/nickname")
    public ResponseEntity<JSONResponse<NicknameValidationResult>> verifyNickname(
            @RequestParam String nickname) {
        NicknameValidationResult result =
                changeNicknameService.validateNicknameChange(nickname);
        return ResponseEntity.ok(JSONResponse.onSuccess(result));
    }

    // 닉네임 변경
    @PutMapping("/nickname")
    public ResponseEntity<JSONResponse<Void>> changeNickname(
            @RequestBody ChangeNicknameRequest request) {
        changeNicknameService.changeNickname(request.newNickname());
        return ResponseEntity.ok(JSONResponse.onSuccess());
    }
}
