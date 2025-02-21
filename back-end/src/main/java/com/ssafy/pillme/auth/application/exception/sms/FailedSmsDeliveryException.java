package com.ssafy.pillme.auth.application.exception.sms;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class FailedSmsDeliveryException extends CommonException {
    public FailedSmsDeliveryException() {
        super(ErrorCode.FAILED_SMS_DELIVERY);
    }
}
