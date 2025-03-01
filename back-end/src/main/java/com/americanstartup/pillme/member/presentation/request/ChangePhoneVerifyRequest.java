package com.americanstartup.pillme.member.presentation.request;

public record ChangePhoneVerifyRequest(String newPhoneNumber, String code) {
}
