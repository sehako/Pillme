package com.ssafy.pillme.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String BLACKLIST_PREFIX = "BL:";
    private static final String PASSWORD_RESET_PREFIX = "PW_RESET:";
    private static final String TEMP_AUTH_PREFIX = "TEMP_AUTH:";

    /**
     * Refresh Token 저장
     */
    public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Refresh Token 조회
     */
    public String findRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Refresh Token 삭제
     */
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * Access Token 블랙리스트 추가
     */
    public void blacklistToken(String token, long expirationTime) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "true", expirationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Access Token 블랙리스트 확인
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 비밀번호 재설정 토큰 저장
     */
    public void savePasswordResetToken(String email, String token, long expirationTime) {
        String key = PASSWORD_RESET_PREFIX + token;
        redisTemplate.opsForValue().set(key, email, expirationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 비밀번호 재설정 토큰으로 이메일 조회
     */
    public String findEmailByResetToken(String token) {
        String key = PASSWORD_RESET_PREFIX + token;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 비밀번호 재설정 토큰 삭제
     */
    public void deletePasswordResetToken(String token) {
        String key = PASSWORD_RESET_PREFIX + token;
        redisTemplate.delete(key);
    }

    /**
     * 임시 인증 상태 저장 (OAuth2 회원가입 시 사용)
     */
    public void saveTempAuthInfo(String email, String provider, long expirationTime) {
        String key = TEMP_AUTH_PREFIX + email;
        redisTemplate.opsForValue().set(key, provider, expirationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 임시 인증 상태 확인 및 삭제 (OAuth2 회원가입 시 사용)
     */
    public String extractTempAuthInfo(String email) {
        String key = TEMP_AUTH_PREFIX + email;
        String provider = redisTemplate.opsForValue().get(key);
        if (provider != null) {
            redisTemplate.delete(key);
        }
        return provider;
    }
}