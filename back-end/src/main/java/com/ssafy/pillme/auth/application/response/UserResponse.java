package com.ssafy.pillme.auth.application.response;

import com.ssafy.pillme.auth.domain.entity.User;
import com.ssafy.pillme.auth.domain.vo.Gender;
import com.ssafy.pillme.auth.domain.vo.Provider;
import com.ssafy.pillme.auth.domain.vo.Role;
import com.ssafy.pillme.auth.domain.vo.UserInfo;

public record UserResponse(
        Long id,
        String email,
        String name,
        String nickname,
        Role role,
        Gender gender,
        String phone,
        String birthday,
        boolean oauth,
        Provider provider
) {
    public static UserResponse from(User user) {
        UserInfo userInfo = user.extractUserInfo();
        return new UserResponse(
                userInfo.id(),
                userInfo.email(),
                userInfo.name(),
                userInfo.nickname(),
                userInfo.role(),
                userInfo.gender(),
                userInfo.phone(),
                userInfo.birthday(),
                userInfo.oauth(),
                userInfo.provider()
        );
    }
}