package com.americanstartup.pillme.auth.presentation.request;

public record VerifyEmailRequest(String email, String code) {
}
