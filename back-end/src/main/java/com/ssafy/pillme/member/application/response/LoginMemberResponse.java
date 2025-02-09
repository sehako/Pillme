package com.ssafy.pillme.member.application.response;

import com.ssafy.pillme.auth.domain.vo.Gender;
import com.ssafy.pillme.auth.domain.vo.Role;
import com.ssafy.pillme.member.domain.entity.LoginMember;

public record LoginMemberResponse(String email, String phone, String birthday, Gender gender, String nickname, Role role, boolean deleted, boolean oauth) {
    public static LoginMemberResponse from(LoginMember member) {
        return new LoginMemberResponse(
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
