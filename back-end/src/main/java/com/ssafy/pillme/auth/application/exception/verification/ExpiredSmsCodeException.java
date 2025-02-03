package com.ssafy.pillme.auth.application.exception.verification;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class ExpiredSmsCodeException extends CommonException {
    public ExpiredSmsCodeException() {
        super(ErrorCode.EXPIRED_SMS_CODE);
    }
}