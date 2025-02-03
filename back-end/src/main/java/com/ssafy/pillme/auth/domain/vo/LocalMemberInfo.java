package com.ssafy.pillme.auth.domain.vo;

public record LocalMemberInfo(
        String name,
        Gender gender,
        Role role,
        String birthday
) {
    public static LocalMemberInfo of(
            String name,
            Gender gender,
            Role role,
            String birthday
    ) {
        return new LocalMemberInfo(name, gender, role, birthday);
    }
}