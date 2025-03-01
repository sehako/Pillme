package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidChangeSmsCodeException extends CommonException {
    public InvalidChangeSmsCodeException() {
        super(ErrorCode.INVALID_SMS_CODE);
    }
}