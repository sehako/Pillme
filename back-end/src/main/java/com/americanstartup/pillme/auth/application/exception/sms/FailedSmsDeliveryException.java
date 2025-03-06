package com.americanstartup.pillme.auth.application.exception.sms;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class FailedSmsDeliveryException extends CommonException {
    public FailedSmsDeliveryException() {
        super(ErrorCode.FAILED_SMS_DELIVERY);
    }
}
