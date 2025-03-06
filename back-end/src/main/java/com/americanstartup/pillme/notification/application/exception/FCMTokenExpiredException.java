package com.americanstartup.pillme.notification.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class FCMTokenExpiredException extends CommonException {
    public FCMTokenExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
