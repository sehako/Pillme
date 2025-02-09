package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class FailedSmsSendException extends CommonException {
    public FailedSmsSendException() {
        super(ErrorCode.FAILED_SMS_DELIVERY);
    }
}
