package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.notification.presentation.request.FCMTokenRequest;

public interface FCMTokenService {
    void createToken(FCMTokenRequest request);
}
