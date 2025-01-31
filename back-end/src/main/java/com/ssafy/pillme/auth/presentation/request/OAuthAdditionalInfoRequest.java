package com.ssafy.pillme.auth.presentation.request;

import com.ssafy.pillme.auth.domain.vo.Gender;
import jakarta.validation.constraints.*;

public record OAuthAdditionalInfoRequest(
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다")
        String nickname,

        @NotNull(message = "성별은 필수입니다")
        Gender gender,

        @NotBlank(message = "휴대전화는 필수입니다")
        @Pattern(regexp = "^01[0-9]{9}$", message = "올바른 휴대전화 형식이 아닙니다")
        String phone,

        @NotBlank(message = "생년월일은 필수입니다")
        @Pattern(regexp = "^\\d{8}$", message = "올바른 생년월일 형식이 아닙니다")
        String birthday
) {
    public static OAuthAdditionalInfoRequest of(
            String nickname,
            Gender gender,
            String phone,
            String birthday
    ) {
        return new OAuthAdditionalInfoRequest(
                nickname,
                gender,
                phone,
                birthday
        );
    }
}