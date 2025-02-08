package com.ssafy.pillme.notification.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class FCMTokenExpiredException extends CommonException {
    public FCMTokenExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
