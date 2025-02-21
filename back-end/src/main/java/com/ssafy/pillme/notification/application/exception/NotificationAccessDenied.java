package com.ssafy.pillme.notification.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class NotificationAccessDenied extends CommonException {
    public NotificationAccessDenied(ErrorCode errorCode) {
        super(errorCode);
    }
}
