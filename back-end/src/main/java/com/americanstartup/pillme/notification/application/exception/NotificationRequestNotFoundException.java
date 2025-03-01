package com.americanstartup.pillme.notification.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NotificationRequestNotFoundException extends CommonException {
    public NotificationRequestNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
