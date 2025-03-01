package com.americanstartup.pillme.member.presentation.request;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
