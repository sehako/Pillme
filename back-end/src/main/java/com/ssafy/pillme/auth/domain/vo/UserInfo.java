package com.ssafy.pillme.auth.domain.vo;

public record UserInfo(
        Long id,
        String email,
        String name,
        String nickname,
        Role role,
        Gender gender,
        String phone,
        String birthday,
        boolean oauth,
        Provider provider
) {}