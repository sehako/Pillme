package com.ssafy.pillme.auth.application.response;

public record OAuth2Response(
        ResponseType type,
        String email,           // 이메일 필드 추가
        String name,           // 이름 필드 추가
        String redirectUrl,
        TokenResponse tokenResponse
) {
    /**
     * 추가 정보 입력이 필요한 신규 회원용 응답 생성
     */
    public static OAuth2Response redirect(String email, String name, String redirectUrl) {
        return new OAuth2Response(ResponseType.REDIRECT, email, name, redirectUrl, null);
    }

    /**
     * 기존 회원 로그인 성공용 토큰 응답 생성
     */
    public static OAuth2Response token(TokenResponse tokenResponse) {
        return new OAuth2Response(ResponseType.TOKEN, null, null, null, tokenResponse);
    }

    /**
     * 응답 타입 정의
     */
    public enum ResponseType {
        REDIRECT,   // 회원정보 입력 페이지로 리다이렉트 (신규 회원)
        TOKEN       // 토큰 발급 (기존 회원)
    }

    /**
     * 추가 정보 입력이 필요한지 확인하는 편의 메서드
     */
    public boolean needAdditionalInfo() {
        return type == ResponseType.REDIRECT;
    }
}