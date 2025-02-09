package com.ssafy.pillme.notification.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class NotificationSettingNotFoundException extends CommonException {
    public NotificationSettingNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
