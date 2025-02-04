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
    INVALID_ACCESS_TOKEN(4018, UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다"),
    INVALID_REFRESH_TOKEN(4017, UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다"),
    INVALID_RESET_TOKEN(4013, BAD_REQUEST, "잘못된 비밀번호 재설정 토큰입니다"),

    // 인증 코드 관련 에러
    INVALID_EMAIL_CODE(4002, BAD_REQUEST, "잘못된 이메일 인증 코드입니다"),
    EXPIRED_EMAIL_CODE(4003, BAD_REQUEST, "만료된 이메일 인증 코드입니다"),
    INVALID_SMS_CODE(4004, BAD_REQUEST, "잘못된 SMS 인증 코드입니다"),
    EXPIRED_SMS_CODE(4005, BAD_REQUEST, "만료된 SMS 인증 코드입니다"),

    // 회원 정보 검증 관련 에러
    DUPLICATE_EMAIL_ADDRESS(4006, BAD_REQUEST, "이미 존재하는 이메일입니다"),
    DUPLICATE_MEMBER_NICKNAME(4007, BAD_REQUEST, "이미 존재하는 닉네임입니다"),
    MISMATCHED_PHONE_NUMBER(4012, BAD_REQUEST, "전화번호가 일치하지 않습니다"),

    // 회원 인증 관련 에러
    INVALID_MEMBER_INFO(4008, BAD_REQUEST, "존재하지 않는 사용자입니다"),
    INVALID_MEMBER_PASSWORD(4009, BAD_REQUEST, "잘못된 비밀번호입니다"),
    INVALID_LOGIN_CREDENTIALS(4014, BAD_REQUEST, "이메일 또는 비밀번호가 틀렸습니다"),
    UNVERIFIED_EMAIL_ADDRESS(4016, BAD_REQUEST, "이메일 인증이 필요합니다"),
    UNVERIFIED_PHONE_NUMBER(4015, BAD_REQUEST, "전화번호 인증이 필요합니다"),

    // 비밀번호 정책 관련 에러
    INVALID_PASSWORD_FORMAT(4019, BAD_REQUEST, "비밀번호는 12자 이상이며 영문 대/소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다"),

    // OAuth 관련 에러
    INVALID_OAUTH_STATE(4010, BAD_REQUEST, "잘못된 OAuth 상태입니다"),
    RESTRICTED_OAUTH_PASSWORD(4011, BAD_REQUEST, "OAuth 사용자는 비밀번호를 재설정할 수 없습니다"),

    // Security Context 관련 에러
    SECURITY_CONTEXT_AUTH_INFO_NOT_FOUND(4020, UNAUTHORIZED, "Security Context에 인증 정보가 없습니다"),
    SECURITY_CONTEXT_ROLE_INFO_NOT_FOUND(4021, UNAUTHORIZED, "Security Context에 권한 정보가 없습니다"),
    INVALID_MEMBER_ID_FORMAT(4022, UNAUTHORIZED, "잘못된 사용자 ID 형식입니다"),

    // 외부 서비스 연동 에러
    FAILED_EMAIL_DELIVERY(5001, INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다"),
    FAILED_SMS_DELIVERY(5002, INTERNAL_SERVER_ERROR, "SMS 발송에 실패했습니다"),

    // 약물 관리
    MEDICATION_NOT_FOUND(4050, NOT_FOUND, "약물을 찾을 수 없습니다"),
    INFORMATION_NOT_FOUND(4051, NOT_FOUND, "현재 요청한 관리 번호는 존재하지 않습니다"),
    INFORMATION_ALREADY_DELETED(4052, NOT_FOUND, "이미 삭제되었거나 존재하지 않는 복약 정보입니다"),
    MANAGEMENT_NOT_FOUND(4053, NOT_FOUND, "요청한 약물 관리 정보는 존재하지 않습니다"),
    INVALID_TIME_REQUEST(4054, BAD_REQUEST, "유효하지 않은 시간 요청입니다"),
    INVALID_MEMBER_REQUEST(4055, UNAUTHORIZED, "다른 사용자의 복약 내역은 수정할 수 없습니다"),
    ;

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
