package com.ssafy.pillme.notification.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidFCMTokenException extends CommonException {
    public InvalidFCMTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
