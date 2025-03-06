package com.americanstartup.pillme.auth.application.exception.verification;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidSmsCodeException extends CommonException {
    public InvalidSmsCodeException() {
        super(ErrorCode.INVALID_SMS_CODE);
    }
}