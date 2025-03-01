package com.americanstartup.pillme.chat.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class MarkMessageAsReadException extends CommonException {
    public MarkMessageAsReadException(ErrorCode errorCode) {
        super(errorCode);
    }
}
