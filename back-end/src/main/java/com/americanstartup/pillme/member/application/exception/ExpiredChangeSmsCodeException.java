package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class ExpiredChangeSmsCodeException extends CommonException {
    public ExpiredChangeSmsCodeException() {
        super(ErrorCode.EXPIRED_SMS_CODE);
    }
}