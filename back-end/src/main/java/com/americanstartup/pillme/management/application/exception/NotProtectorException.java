package com.americanstartup.pillme.management.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NotProtectorException extends CommonException {
    public NotProtectorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
