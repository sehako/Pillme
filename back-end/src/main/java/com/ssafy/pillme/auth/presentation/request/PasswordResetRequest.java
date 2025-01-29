package com.ssafy.pillme.auth.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordResetRequest(
        @NotBlank(message = "토큰은 필수입니다")
        String token,

        @NotBlank(message = "새 비밀번호는 필수입니다")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상이며 영문자, 숫자, 특수문자를 포함해야 합니다")
        String newPassword
) {
    public static PasswordResetRequest of(String token, String newPassword) {
        return new PasswordResetRequest(token, newPassword);
    }
}