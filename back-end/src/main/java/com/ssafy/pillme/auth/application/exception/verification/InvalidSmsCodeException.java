package com.ssafy.pillme.auth.application.exception.verification;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidSmsCodeException extends CommonException {
    public InvalidSmsCodeException() {
        super(ErrorCode.INVALID_SMS_CODE);
    }
}