package com.americanstartup.pillme.management.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NoInformationException extends CommonException {
    public NoInformationException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
