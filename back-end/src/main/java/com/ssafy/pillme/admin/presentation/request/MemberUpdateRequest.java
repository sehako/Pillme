package com.ssafy.pillme.admin.presentation.request;

import com.ssafy.pillme.auth.domain.vo.Gender;

public record MemberUpdateRequest(
        String name,
        String email,
        String phone,
        String nickname,
        String birthday,
        Gender gender
) {}
