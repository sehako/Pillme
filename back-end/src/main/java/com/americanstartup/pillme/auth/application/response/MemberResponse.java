package com.americanstartup.pillme.auth.application.response;

import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.auth.domain.vo.Gender;
import com.americanstartup.pillme.auth.domain.vo.Provider;
import com.americanstartup.pillme.auth.domain.vo.Role;

public record MemberResponse(
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
) {
    public static MemberResponse from(Member user) {
        return new MemberResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getRole(),
                user.getGender(),
                user.getPhone(),
                user.getBirthday(),
                user.isOauth(),
                user.getProvider()
        );
    }
}
