package com.americanstartup.pillme.notification.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidFCMTokenException extends CommonException {
    public InvalidFCMTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
