package com.americanstartup.pillme.notification.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class FCMTokenNotFoundException extends CommonException {
    public FCMTokenNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
