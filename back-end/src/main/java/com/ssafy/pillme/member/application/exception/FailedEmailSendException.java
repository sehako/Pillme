package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class FailedEmailSendException extends CommonException {
    public FailedEmailSendException() {
        super(ErrorCode.FAILED_EMAIL_DELIVERY);
    }
}