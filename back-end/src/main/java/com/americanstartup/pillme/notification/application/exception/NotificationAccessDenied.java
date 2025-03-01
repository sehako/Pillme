package com.americanstartup.pillme.notification.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NotificationAccessDenied extends CommonException {
    public NotificationAccessDenied(ErrorCode errorCode) {
        super(errorCode);
    }
}
