package com.ssafy.pillme.auth.domain.vo;

public record NaverUserInfo(
        String email,
        String name,
        String nickname,
        Gender gender,
        String phone,
        String birthday
) {
    public static NaverUserInfo of(
            String email,
            String name,
            String nickname,
            Gender gender,
            String birthyear,
            String birthday,
            String mobile
    ) {
        String formattedBirthday = birthyear + birthday.replace("-", "");
        String formattedPhone = mobile.replaceAll("[^0-9]", "");

        return new NaverUserInfo(
                email,
                name,
                nickname,
                gender,
                formattedPhone,
                formattedBirthday
        );
    }
}
