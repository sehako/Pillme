package com.americanstartup.pillme.auth.presentation.request;

import com.americanstartup.pillme.auth.domain.vo.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OAuthSignUpRequest(
        @NotBlank(message = "이메일은 필수입니다")
        String email,

        @NotBlank(message = "이름은 필수입니다")
        String name,

        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다")
        String nickname,

        Gender gender,
        String phone,
        String birthday
) {
    public static OAuthSignUpRequest of(
            String email,
            String name,
            String nickname,
            Gender gender,
            String phone,
            String birthday
    ) {
        return new OAuthSignUpRequest(
                email,
                name,
                nickname,
                gender,
                phone,
                birthday
        );
    }
}