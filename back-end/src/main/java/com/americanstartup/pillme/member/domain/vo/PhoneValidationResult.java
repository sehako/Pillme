package com.americanstartup.pillme.member.domain.vo;

public record PhoneValidationResult(
        boolean isSameAsCurrent,
        boolean isAlreadyExists,
        boolean isValidFormat
) {
}