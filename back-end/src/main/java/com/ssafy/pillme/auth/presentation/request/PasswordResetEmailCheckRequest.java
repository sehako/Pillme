package com.ssafy.pillme.auth.presentation.request;

import jakarta.validation.constraints.Email;

public record PasswordResetEmailCheckRequest(
        @Email
        String email
) {}
