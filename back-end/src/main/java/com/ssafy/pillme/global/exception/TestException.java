package com.ssafy.pillme.global.exception;

import com.ssafy.pillme.global.code.ErrorCode;

public class TestException extends CommonException {
    public TestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
