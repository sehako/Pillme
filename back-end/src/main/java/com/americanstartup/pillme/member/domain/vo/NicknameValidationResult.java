package com.americanstartup.pillme.member.domain.vo;

public record NicknameValidationResult(boolean isSameAsCurrent,
                                       boolean isAlreadyExists) {
}
