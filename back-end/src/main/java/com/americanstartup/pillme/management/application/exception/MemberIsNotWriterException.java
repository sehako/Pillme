package com.americanstartup.pillme.management.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class MemberIsNotWriterException extends CommonException {
    public MemberIsNotWriterException(ErrorCode errorCode) {
        super(errorCode);
    }
}
