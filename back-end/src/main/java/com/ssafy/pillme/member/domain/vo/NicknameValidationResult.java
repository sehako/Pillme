package com.ssafy.pillme.member.domain.vo;

public record NicknameValidationResult(boolean isSameAsCurrent,
                                       boolean isAlreadyExists) {
}
