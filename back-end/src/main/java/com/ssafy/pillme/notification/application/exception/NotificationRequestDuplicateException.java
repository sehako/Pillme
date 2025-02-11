package com.ssafy.pillme.notification.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class NotificationRequestDuplicateException extends CommonException {
    public NotificationRequestDuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
