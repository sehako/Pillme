package com.ssafy.pillme.member.domain.vo;

import com.ssafy.pillme.auth.domain.vo.Role;

public record LoginMemberInfo (
        String email,
        String name,
        String nickname,
        Role role
){
    public static LoginMemberInfo of(
            String email,
            String name,
            String nickname,
            Role role
    ){
        return new LoginMemberInfo(email, name, nickname, role);
    }
}
