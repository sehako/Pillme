package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.notification.domain.entity.FCMToken;
import com.ssafy.pillme.notification.presentation.request.FCMTokenRequest;

import java.util.List;
import java.util.Optional;

public interface FCMTokenService {
    void createToken(FCMTokenRequest request);

    // 사용자 id로 토큰들 조회
    Optional<List<FCMToken>> findByUserId(Integer userId);
}
