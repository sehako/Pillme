package com.americanstartup.pillme.global.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    private final ErrorCode errorCode;

    public CommonException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}