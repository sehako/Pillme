package com.ssafy.pillme.notification.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class FCMTokenNotFoundException extends CommonException {
    public FCMTokenNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
