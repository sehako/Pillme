package com.ssafy.pillme.auth.presentation.request;

public record VerifyEmailRequest(String email, String code) {
}
