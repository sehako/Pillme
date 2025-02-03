package com.ssafy.pillme.global.code;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    REQUEST_SUCCESS(2000, OK, "요청이 성공적으로 완료되었습니다"),
    INFORMATION_SAVE_SUCCESS(2001, CREATED, "복약 정보가 성공적으로 등록되었습니다"),
    ;

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}