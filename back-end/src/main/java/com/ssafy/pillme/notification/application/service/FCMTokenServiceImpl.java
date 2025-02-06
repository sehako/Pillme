package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.notification.domain.entity.FCMToken;
import com.ssafy.pillme.notification.infrastructure.repository.FCMTokenRepository;
import com.ssafy.pillme.notification.presentation.request.FCMTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FCMTokenServiceImpl implements FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;

    @Override
    public void createToken(FCMTokenRequest request, Member loginMember) {
        fcmTokenRepository.save(FCMToken.create(loginMember, request.token()));
    }

    @Override
    public List<FCMToken> findAllByMemberId(Long memberId) {
        return fcmTokenRepository.findAllByMemberId(memberId);
    }
}
