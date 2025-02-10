package com.ssafy.pillme.member.presentation.request;

public record UpdateLoginMemberRequest(
        String nickname,
        String email,
        String phone
) {
}
