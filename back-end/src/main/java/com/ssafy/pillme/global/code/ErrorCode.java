package com.ssafy.pillme.global.code;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 서버 에러
    SERVER_ERROR(5000, INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),

    // 요청 관련 에러
    INVALID_REQUEST(4000, BAD_REQUEST, "잘못된 요청 형식입니다"),
    INVALID_AUTHORIZATION_CODE(4001, BAD_REQUEST, "유효하지 않은 인증 코드입니다"),

    // 토큰 관련 에러
    INVALID_ACCESS_TOKEN(4101, UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다"),
    INVALID_REFRESH_TOKEN(4102, UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다"),
    INVALID_RESET_TOKEN(4103, BAD_REQUEST, "잘못된 비밀번호 재설정 토큰입니다"),
    DENYLISTED_TOKEN(4104, UNAUTHORIZED, "이미 로그아웃된 토큰입니다"),

    // 인증 코드 관련 에러
    INVALID_EMAIL_CODE(4111, BAD_REQUEST, "잘못된 이메일 인증 코드입니다"),
    EXPIRED_EMAIL_CODE(4112, BAD_REQUEST, "만료된 이메일 인증 코드입니다"),
    INVALID_SMS_CODE(4113, BAD_REQUEST, "잘못된 SMS 인증 코드입니다"),
    EXPIRED_SMS_CODE(4114, BAD_REQUEST, "만료된 SMS 인증 코드입니다"),

    // 회원 정보 검증 관련 에러
    DUPLICATE_EMAIL_ADDRESS(4121, BAD_REQUEST, "이미 존재하는 이메일입니다"),
    DUPLICATE_MEMBER_NICKNAME(4122, BAD_REQUEST, "이미 존재하는 닉네임입니다"),
    DUPLICATE_PHONE_NUMBER(4123, BAD_REQUEST, "이미 존재하는 전화번호입니다"),
    MISMATCHED_PHONE_NUMBER(4124, BAD_REQUEST, "전화번호가 일치하지 않습니다"),
    INVALID_PHONE_NUMBER_FORMAT(4125, BAD_REQUEST, "유효하지 않은 전화번호 형식입니다"),
    INVALID_EMAIL_ADDRESS_FORMAT(4126, BAD_REQUEST, "유효하지 않은 이메일 형식입니다"),

    // 회원 인증 관련 에러
    INVALID_MEMBER_INFO(4131, BAD_REQUEST, "존재하지 않는 사용자입니다"),
    INVALID_MEMBER_PASSWORD(4132, BAD_REQUEST, "잘못된 비밀번호입니다"),
    INVALID_LOGIN_CREDENTIALS(4133, BAD_REQUEST, "이메일 또는 비밀번호가 틀렸습니다"),
    UNVERIFIED_EMAIL_ADDRESS(4134, BAD_REQUEST, "이메일 인증이 필요합니다"),
    UNVERIFIED_PHONE_NUMBER(4135, BAD_REQUEST, "전화번호 인증이 필요합니다"),

    // 비밀번호 정책 관련 에러
    INVALID_PASSWORD_FORMAT(4141, BAD_REQUEST, "비밀번호는 12자 이상이며 영문 대/소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다"),

    // OAuth 관련 에러
    INVALID_OAUTH_STATE(4151, BAD_REQUEST, "잘못된 OAuth 상태입니다"),
    RESTRICTED_OAUTH_PASSWORD(4152, BAD_REQUEST, "OAuth 사용자는 비밀번호를 재설정할 수 없습니다"),

    // Security Context 관련 에러
    SECURITY_CONTEXT_AUTH_INFO_NOT_FOUND(4161, UNAUTHORIZED, "사용자 인증 정보가 없습니다"),
    SECURITY_CONTEXT_ROLE_INFO_NOT_FOUND(4162, UNAUTHORIZED, "사용자 권한 정보가 없습니다"),
    INVALID_MEMBER_ID_FORMAT(4163, UNAUTHORIZED, "잘못된 사용자 ID 형식입니다"),

    // SMS 관련 에러
    INVALID_SMS_API_KEY(4171, UNAUTHORIZED, "유효하지 않은 SMS API 키입니다"),
    FAILED_SMS_DELIVERY(4172, INTERNAL_SERVER_ERROR, "SMS 메시지 전송에 실패했습니다"),

    // email 관련 에러
    FAILED_EMAIL_DELIVERY(5101, INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다"),

    // 약물 관리
    MEDICATION_NOT_FOUND(4050, NOT_FOUND, "약물을 찾을 수 없습니다"),
    INFORMATION_NOT_FOUND(4051, NOT_FOUND, "현재 요청한 관리 번호는 존재하지 않습니다"),
    INFORMATION_ALREADY_DELETED(4052, NOT_FOUND, "이미 삭제되었거나 존재하지 않는 복약 정보입니다"),
    MANAGEMENT_NOT_FOUND(4053, NOT_FOUND, "요청한 약물 관리 정보는 존재하지 않습니다"),
    INVALID_TIME_REQUEST(4054, BAD_REQUEST, "유효하지 않은 시간 요청입니다"),
    INVALID_MEMBER_REQUEST(4055, UNAUTHORIZED, "다른 사용자의 복약 내역은 수정할 수 없습니다"),

    //채팅방 관리
    EMPTY_CHATROOM_ID(4070, BAD_REQUEST, "요청한 채팅방은 존재하지 않습니다."),

    // 복약 내역
    HISTORY_NOT_FOUND(4100, NOT_FOUND, "복약 내역을 찾을 수 없습니다."),
    MEMBER_NOT_MATCHED(4101, UNAUTHORIZED, "복약 내역은 사용자 본인만 삭제 가능합니다"),

    // FCM Token
    FCM_TOKEN_NOT_FOUND(4200, NOT_FOUND, "FCM 토큰을 찾을 수 없습니다"),

    // 알림
    NOTIFICATION_SETTING_NOT_FOUND(4300, NOT_FOUND, "알림 설정을 찾을 수 없습니다"),
    NOTIFICATION_ACCESS_DENIED(4301, UNAUTHORIZED, "알림 설정에 접근할 수 없습니다"),

    // 관계
    DEPENDENCY_NOT_FOUND(4400, NOT_FOUND, "등록된 관계를 찾을 수 없습니다"),
    DUPLICATE_DEPENDENCY(4401, BAD_REQUEST, "이미 등록된 관계입니다");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
