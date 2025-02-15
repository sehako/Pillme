package com.ssafy.pillme.auth.presentation.request;

public record PasswordResetEmailSendRequest(String email, String phone) {
}
