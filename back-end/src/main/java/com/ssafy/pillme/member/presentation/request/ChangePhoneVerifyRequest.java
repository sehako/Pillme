package com.ssafy.pillme.member.presentation.request;

public record ChangePhoneVerifyRequest(String newPhoneNumber, String code) {
}
