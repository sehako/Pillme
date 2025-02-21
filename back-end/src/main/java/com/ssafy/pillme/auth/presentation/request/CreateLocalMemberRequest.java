package com.ssafy.pillme.auth.presentation.request;

import com.ssafy.pillme.auth.domain.vo.Gender;
import com.ssafy.pillme.dependency.presentation.request.LocalMemberRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLocalMemberRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @NotBlank(message = "생년월일은 필수입니다.")
        String birthday
) {
    public static CreateLocalMemberRequest from(LocalMemberRequest request) {
        return new CreateLocalMemberRequest(
                request.name(),
                request.gender(),
                request.birthday()
        );
    }
}