package com.ssafy.pillme.member.presentation.request;

public record UpdatePasswordRequest(String currentPassword, String newPassword) {
}
