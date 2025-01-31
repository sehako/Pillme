package com.ssafy.pillme.auth.domain.vo;

public record GoogleUserInfo(
        String email,
        String name,
        String nickname
) {}