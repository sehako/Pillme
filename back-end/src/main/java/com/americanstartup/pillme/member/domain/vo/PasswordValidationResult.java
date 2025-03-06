package com.americanstartup.pillme.member.domain.vo;

public record PasswordValidationResult(
        boolean isCurrentPasswordValid,
        boolean isNewPasswordValid
) {}
