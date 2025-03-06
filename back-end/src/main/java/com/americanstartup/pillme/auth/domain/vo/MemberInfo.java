package com.americanstartup.pillme.auth.domain.vo;

import com.americanstartup.pillme.auth.domain.entity.Member;

public record MemberInfo (
        Long id,
        String email,
        String name,
        String nickname,
        Role role,
        Gender gender,
        String phone,
        String birthday,
        Provider provider,
        boolean oauth
){
    public static MemberInfo from(Member member) {
        return new MemberInfo(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                member.getRole(),
                member.getGender(),
                member.getPhone(),
                member.getBirthday(),
                member.getProvider(),
                member.isOauth()
        );
    }
}
