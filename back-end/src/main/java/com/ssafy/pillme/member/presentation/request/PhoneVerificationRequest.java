package com.ssafy.pillme.member.presentation.request;

public record PhoneVerificationRequest(
        String phoneNumber,
        String code
) {
}