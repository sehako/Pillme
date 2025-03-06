package com.americanstartup.pillme.notification.domain.vo;

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
    DEPENDENCY_DELETE_REQUEST("DEPENDENCY_DELETE_REQUEST", "관계 삭제 요청", "관계 삭제를 요청했습니다."),
    DEPENDENCY_DELETE_ACCEPT("DEPENDENCY_DELETE_ACCEPT", "관계 삭제 수락", "관계 삭제가 수락되었습니다."),
    DEPENDENCY_DELETE_REJECT("DEPENDENCY_DELETE_REJECT", "관계 삭제 거절", "관계 삭제가 거절되었습니다."),

    // 보호자 -> 피보호자 약 복용 얄림
    MEDICINE_TAKE_REMINDER("MEDICINE_TAKE_REMINDER", "약 복용 요청 알림", "약 복용을 요청했습니다."),

    // 채팅 메시지 알림
    CHAT_MESSAGE("CHAT_MESSAGE", "새로운 메시지", "메시지를 보냈습니다."),

    // 처방전 알림
    PRESCRIPTION_REQUEST("PRESCRIPTION_REQUEST", "처방전 등록 요청", "복약 정보 등록을 요청했습니다."),
    PRESCRIPTION_ACCEPT("PRESCRIPTION_ACCEPT", "처방전 등록 수락", "복약 정보 등록이 수락되었습니다."),
    PRESCRIPTION_REJECT("PRESCRIPTION_REJECT", "처방전 등록 거절", "복약 정보 등록이 거절되었습니다."),
    PRESCRIPTION_DELETE_REQUEST("PRESCRIPTION_DELETE_REQUEST", "처방전 삭제 요청", "복약 정보 삭제를 요청했습니다."),
    PRESCRIPTION_DELETE_ACCEPT("PRESCRIPTION_DELETE_ACCEPT", "처방전 삭제 수락", "복약 정보 삭제가 수락되었습니다."),
    PRESCRIPTION_DELETE_REJECT("PRESCRIPTION_DELETE_REJECT", "처방전 삭제 거절", "복약 정보 삭제가 거절되었습니다."),
    ANALYSIS_COMPLETE("ANALYSIS_COMPLETE", "이미지 분석 완료", "요청하신 이미지 분석이 완료되었습니다.");

    // 어떤 종류의 알림인지 구분하기 위한 코드
    private final String code;
    // 알림 제목
    private final String title;
    // 알림 내용
    private final String message;
}
