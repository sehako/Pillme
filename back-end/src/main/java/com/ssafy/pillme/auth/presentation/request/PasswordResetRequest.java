package com.ssafy.pillme.auth.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordResetRequest(
        @NotBlank(message = "토큰은 필수입니다")
        String token,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&\\[\\]\\{\\}\\(\\)\\-_+=<>,.;:'\"`~\\\\|/])[A-Za-z\\d@$!%*#?&\\[\\]\\{\\}\\(\\)\\-_+=<>,.;:'\"`~\\\\|/]{12,}$",
                message = "비밀번호는 12자 이상이며 영문 대/소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다"
        )
        String newPassword
) {
    public static PasswordResetRequest of(String token, String newPassword) {
        return new PasswordResetRequest(token, newPassword);
    }
}