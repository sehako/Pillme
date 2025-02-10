package com.ssafy.pillme.member.presentation.request;

public record ChangeEmailVerifyRequest(String newEmail, String code) {
}
