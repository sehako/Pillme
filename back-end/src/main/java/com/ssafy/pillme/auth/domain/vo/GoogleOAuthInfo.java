package com.ssafy.pillme.auth.domain.vo;

public record GoogleOAuthInfo(
        String email,
        String name
) {}