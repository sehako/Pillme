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
            String gender,
            String birthyear,
            String birthday,
            String mobile
    ) {
        // 성별 변환 (제공되지 않으면 null)
        Gender convertedGender = gender != null ?
                gender.toUpperCase().equals("F") ? Gender.F : Gender.M
                : null;

        // 생년월일 포맷팅 (제공되지 않으면 null)
        String formattedBirthday = (birthyear != null && birthday != null) ?
                birthyear + birthday.replace("-", "")
                : null;

        // 전화번호 포맷팅 (제공되지 않으면 null)
        String formattedPhone = mobile != null ?
                mobile.replaceAll("[^0-9]", "")
                : null;

        return new NaverUserInfo(
                email,
                name,
                nickname,
                convertedGender,
                formattedPhone,
                formattedBirthday
        );
    }
}