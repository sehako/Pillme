package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.notification.domain.entity.FCMToken;
import com.ssafy.pillme.notification.presentation.request.FCMTokenRequest;

import java.util.List;

public interface FCMTokenService {
    void createToken(FCMTokenRequest request);

    // 사용자 id로 토큰들 조회
    List<FCMToken> findAllByMemberId(Long memberId);
}
