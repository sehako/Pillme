package com.americanstartup.pillme.member.domain.vo;

import com.americanstartup.pillme.auth.domain.vo.Role;

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
