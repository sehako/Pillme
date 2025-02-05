package com.ssafy.pillme.notification.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationCode {
    DEPENDENCY_REQUEST("DEPENDENCY_REQUEST", "관계 등록 요청", "관계 등록을 요청했습니다."),
    DEPENDENCY_ACCEPT("DEPENDENCY_ACCEPT", "관계 요청 수락", "관계 등록이 수락되었습니다."),
    DEPENDENCY_REJECT("DEPENDENCY_REJECT", "관계 요청 거절", "관계 등록이 거절되었습니다."),
    MEDICINE_REQUEST("MEDICINE_REQUEST", "약 등록 요청", "약 등록을 요청했습니다."),
    MEDICINE_ACCEPT("MEDICINE_ACCEPT", "약 등록 수락", "약 등록이 수락되었습니다."),
    MEDICINE_REJECT("MEDICINE_REJECT", "약 등록 거절", "약 등록이 거절되었습니다."),
    DEPENDENCY_DELETE_REQUEST("DEPENDENCY_DELETE_REQUEST", "관계 삭제 요청", "관계 삭제를 요청했습니다.");

    // 어떤 종류의 알림인지 구분하기 위한 코드
    private final String code;
    // 알림 제목
    private final String title;
    // 알림 내용
    private final String message;
}
