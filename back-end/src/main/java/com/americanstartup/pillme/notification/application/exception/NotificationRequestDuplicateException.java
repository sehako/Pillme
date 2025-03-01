package com.americanstartup.pillme.notification.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NotificationRequestDuplicateException extends CommonException {
    public NotificationRequestDuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
