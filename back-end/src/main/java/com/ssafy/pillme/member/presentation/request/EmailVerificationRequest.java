package com.ssafy.pillme.member.presentation.request;

public record EmailVerificationRequest(
        String email,
        String code
) {
}
