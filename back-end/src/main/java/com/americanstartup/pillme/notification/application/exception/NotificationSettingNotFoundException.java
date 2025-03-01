package com.americanstartup.pillme.notification.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NotificationSettingNotFoundException extends CommonException {
    public NotificationSettingNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
