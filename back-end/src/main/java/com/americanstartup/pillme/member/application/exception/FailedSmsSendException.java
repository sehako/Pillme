package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class FailedSmsSendException extends CommonException {
    public FailedSmsSendException() {
        super(ErrorCode.FAILED_SMS_DELIVERY);
    }
}
