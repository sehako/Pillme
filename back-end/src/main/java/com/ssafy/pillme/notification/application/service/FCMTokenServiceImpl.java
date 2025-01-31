package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.notification.domain.entity.FCMToken;
import com.ssafy.pillme.notification.infrastructure.repository.FCMTokenRepository;
import com.ssafy.pillme.notification.presentation.request.FCMTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FCMTokenServiceImpl implements FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;

    @Override
    public void createToken(FCMTokenRequest request) {
        //TODO: 회원 데이터 추가 필요
        fcmTokenRepository.save(FCMToken.create(1, request.token()));
    }

    @Override
    public Optional<List<FCMToken>> findByUserId(Integer userId) {
        return fcmTokenRepository.findByUserId(userId);
    }
}
