package com.ssafy.pillme.auth.presentation.request;

import com.ssafy.pillme.auth.domain.vo.Gender;
import jakarta.validation.constraints.*;

public record SignUpRequest(
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상이며 숫자, 문자, 특수문자를 포함해야 합니다")
        String password,

        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 30, message = "이름은 2자 이상 30자 이하여야 합니다")
        String name,

        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다")
        String nickname,

        @NotNull(message = "성별은 필수입니다")
        Gender gender,

        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$",
                message = "올바른 전화번호 형식이 아닙니다")
        String phone,

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
                message = "올바른 생년월일 형식이 아닙니다")
        String birthday
) {
    public static SignUpRequest of(String email, String password, String name,
                                   String nickname, Gender gender, String phone,
                                   String birthday) {
        return new SignUpRequest(email, password, name, nickname, gender, phone, birthday);
    }
}