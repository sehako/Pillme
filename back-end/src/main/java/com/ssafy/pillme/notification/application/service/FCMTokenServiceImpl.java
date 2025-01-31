package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.auth.infrastructure.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    public void createToken(FCMTokenRequest request) {
        //TODO: 회원 데이터 추가 필요
        fcmTokenRepository.save(FCMToken.create(userRepository.findById(1L).get(), request.token()));
    }

    @Override
    public Optional<List<FCMToken>> findByMemberId(Long memberId) {
        return fcmTokenRepository.findByMemberId(memberId);
    }
}
