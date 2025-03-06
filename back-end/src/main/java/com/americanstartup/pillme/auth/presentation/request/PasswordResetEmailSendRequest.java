package com.americanstartup.pillme.auth.presentation.request;

public record PasswordResetEmailSendRequest(String email, String phone) {
}
