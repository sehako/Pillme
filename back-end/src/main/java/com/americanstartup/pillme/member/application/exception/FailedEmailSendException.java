package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class FailedEmailSendException extends CommonException {
    public FailedEmailSendException() {
        super(ErrorCode.FAILED_EMAIL_DELIVERY);
    }
}