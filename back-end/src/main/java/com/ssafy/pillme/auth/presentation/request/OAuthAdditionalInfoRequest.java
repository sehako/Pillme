package com.ssafy.pillme.auth.presentation.request;

import com.ssafy.pillme.auth.domain.vo.Gender;
import com.ssafy.pillme.auth.domain.vo.GoogleOAuthInfo;
import com.ssafy.pillme.auth.domain.vo.NaverOAuthInfo;
import jakarta.validation.constraints.*;

public record OAuthAdditionalInfoRequest(
        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 30, message = "이름은 2자 이상 30자 이하여야 합니다")
        String name,

        @NotBlank(message = "이메일은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 30자 이하여야 합니다")
        String email,

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
    public static OAuthAdditionalInfoRequest fromGoogle(GoogleOAuthInfo googleInfo) {
        return new OAuthAdditionalInfoRequest(
                googleInfo.name(),
                googleInfo.email(),
                googleInfo.nickname(),
                null,
                null,
                null
        );
    }

    public static OAuthAdditionalInfoRequest fromNaver(NaverOAuthInfo naverInfo) {
        return new OAuthAdditionalInfoRequest(
                naverInfo.name(),
                naverInfo.email(),
                naverInfo.nickname(),
                naverInfo.gender(),
                naverInfo.phone(),
                naverInfo.birthday()
        );
    }
}