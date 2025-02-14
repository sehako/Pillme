package com.ssafy.pillme.member.domain.vo;

public record EmailValidationResult(boolean isSameAsCurrent,
                                    boolean isAlreadyExists) {
}
