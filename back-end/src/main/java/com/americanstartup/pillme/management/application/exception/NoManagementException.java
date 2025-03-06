package com.americanstartup.pillme.management.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NoManagementException extends CommonException {
    public NoManagementException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
