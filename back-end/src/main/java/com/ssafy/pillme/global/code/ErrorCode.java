package com.ssafy.pillme.global.code;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    SERVER_ERROR(5000, INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(4000, BAD_REQUEST),
    INVALID_AUTHORIZATION_CODE(4000, BAD_REQUEST),
    ;

    private final int code;
    private final HttpStatus httpStatus;
}