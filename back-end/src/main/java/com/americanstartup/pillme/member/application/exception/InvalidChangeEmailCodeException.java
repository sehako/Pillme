package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidChangeEmailCodeException extends CommonException {
    public InvalidChangeEmailCodeException() {
        super(ErrorCode.INVALID_EMAIL_CODE);
    }
}