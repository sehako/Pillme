package com.americanstartup.pillme.management.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class MemberIsNotReaderException extends CommonException {
    public MemberIsNotReaderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
