package com.americanstartup.pillme.auth.application.exception.verification;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class ExpiredSmsCodeException extends CommonException {
    public ExpiredSmsCodeException() {
        super(ErrorCode.EXPIRED_SMS_CODE);
    }
}