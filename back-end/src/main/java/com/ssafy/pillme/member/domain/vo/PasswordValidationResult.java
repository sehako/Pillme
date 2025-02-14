package com.ssafy.pillme.member.domain.vo;

public record PasswordValidationResult(
        boolean isCurrentPasswordValid,
        boolean isNewPasswordValid
) {}
