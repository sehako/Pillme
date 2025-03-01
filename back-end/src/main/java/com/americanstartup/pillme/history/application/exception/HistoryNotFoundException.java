package com.americanstartup.pillme.history.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class HistoryNotFoundException extends CommonException {
    public HistoryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
