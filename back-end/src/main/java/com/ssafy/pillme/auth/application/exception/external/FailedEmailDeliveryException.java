package com.ssafy.pillme.auth.application.exception.external;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class FailedEmailDeliveryException extends CommonException {
    public FailedEmailDeliveryException() {
        super(ErrorCode.FAILED_EMAIL_DELIVERY);
    }
}