package com.americanstartup.pillme.member.application.response;

import com.americanstartup.pillme.auth.domain.vo.Gender;
import com.americanstartup.pillme.auth.domain.vo.Role;
import com.americanstartup.pillme.member.domain.entity.LoginMember;

public record LoginMemberResponse(String name, String email, String phone, String birthday, Gender gender, String nickname, Role role, boolean deleted, boolean oauth) {
    public static LoginMemberResponse from(LoginMember member) {
        return new LoginMemberResponse(
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getBirthday(),
                member.getGender(),
                member.getNickname(),
                member.getRole(),
                member.isDeleted(),
                member.isOauth()
        );
    }
}
