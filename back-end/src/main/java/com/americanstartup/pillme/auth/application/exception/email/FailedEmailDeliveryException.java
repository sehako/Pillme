package com.americanstartup.pillme.auth.application.exception.email;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class FailedEmailDeliveryException extends CommonException {
    public FailedEmailDeliveryException() {
        super(ErrorCode.FAILED_EMAIL_DELIVERY);
    }
}