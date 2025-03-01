package com.americanstartup.pillme.management.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidRegistrationTypeException extends CommonException {
    public InvalidRegistrationTypeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
