package com.americanstartup.pillme.management.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidTimeSelectException extends CommonException {
    public InvalidTimeSelectException(ErrorCode errorCode) {
        super(errorCode);
    }
}
