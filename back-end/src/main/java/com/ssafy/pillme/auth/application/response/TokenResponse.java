package com.ssafy.pillme.auth.application.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        Long accessTokenExpirationTime,
        Long refreshTokenExpirationTime
) {
    public static TokenResponse of(String accessToken, String refreshToken,
                                   Long accessTokenExpirationTime, Long refreshTokenExpirationTime) {
        return new TokenResponse(accessToken, refreshToken,
                accessTokenExpirationTime, refreshTokenExpirationTime);
    }
}