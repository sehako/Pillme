package com.ssafy.pillme.auth.application.response;

public record OAuth2Response(
        ResponseType type,
        String redirectUrl,
        TokenResponse tokenResponse
) {
    /**
     * 리다이렉션 응답 생성
     */
    public static OAuth2Response redirect(String redirectUrl) {
        return new OAuth2Response(ResponseType.REDIRECT, redirectUrl, null);
    }

    /**
     * 토큰 응답 생성
     */
    public static OAuth2Response token(TokenResponse tokenResponse) {
        return new OAuth2Response(ResponseType.TOKEN, null, tokenResponse);
    }

    /**
     * 응답 타입 정의
     */
    public enum ResponseType {
        REDIRECT,   // 회원정보 입력 페이지로 리다이렉트
        TOKEN       // 토큰 발급
    }
}