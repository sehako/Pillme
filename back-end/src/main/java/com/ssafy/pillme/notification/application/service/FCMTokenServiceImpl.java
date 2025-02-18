package com.ssafy.pillme.notification.application.service;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.notification.application.exception.FCMTokenNotFoundException;
import com.ssafy.pillme.notification.application.exception.InvalidFCMTokenException;
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

    /*
     * 토큰 관리 시나리오
     * 1. 로그인 시에 기존 fcm 토큰이 존재하는지 체크
     *    1-1. 없다면 알림 허용 요청
     *    1-2. fcm 토큰 저장
     * 2. 로그인 시에 fcm 토큰이 존재했다면 재사용
     * 3. 로그아웃 시에 fcm 토큰 삭제 요청
     * */

    @Override
    public void createToken(FCMTokenRequest request, Member loginMember) {
        // 1. 토큰 유효성 검사
        if (request.token() == null || request.token().isEmpty()) {
            throw new InvalidFCMTokenException(ErrorCode.INVALID_FCM_TOKEN);
        }

        // 2. 현재 활성화된 같은 토큰이 있는지 확인
        Optional<FCMToken> existingToken = fcmTokenRepository.findByTokenAndDeletedIsFalse(request.token());

        if (existingToken.isPresent()) {
            // 2-1. 같은 사용자의 토큰인 경우, 그대로 사용
            if (existingToken.get().getMember().getId().equals(loginMember.getId())) {
                return;
            }
            // 2-2. 다른 사용자의 토큰인 경우, 기존 토큰을 삭제 (한 기기에서 여러 사용자가 로그인하는 경우 처리)
            existingToken.get().delete();
        }

        // 3. 같은 토큰의 삭제된 토큰 중 현재 사용자의 토큰이 있는지 확인
        Optional<FCMToken> deletedToken = fcmTokenRepository.findByTokenAndMemberIdAndDeletedIsTrue(
                request.token(),
                loginMember.getId()
        );

        if (deletedToken.isPresent()) {
            // 3-1. 삭제된 토큰을 활성화
            deletedToken.get().activate();
            return;
        }

        // 4. 새로운 토큰 저장
        fcmTokenRepository.save(FCMToken.create(loginMember, request.token()));
    }

    @Override
    public List<FCMToken> findAllByMemberId(Long memberId) {
        return fcmTokenRepository.findAllByMemberIdAndDeletedIsFalse(memberId);
    }

    @Override
    public void deleteFCMToken(FCMToken fcmToken) {
        fcmToken.delete();
    }

    @Override
    public void deleteFCMToken(String token, Member loginMember) {
        FCMToken fcmToken = fcmTokenRepository.findByMemberIdAndTokenAndDeletedIsFalse(loginMember.getId(), token)
                .orElseThrow(() -> new FCMTokenNotFoundException(ErrorCode.FCM_TOKEN_NOT_FOUND));

        fcmToken.delete();
    }
}
