package com.americanstartup.pillme.admin.presentation.request;

import com.americanstartup.pillme.auth.domain.vo.Gender;

public record MemberUpdateRequest(
        String name,
        String email,
        String phone,
        String nickname,
        String birthday,
        Gender gender
) {}
