package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidNewPasswordFormatException extends CommonException {
    public InvalidNewPasswordFormatException() {
        super(ErrorCode.INVALID_PASSWORD_FORMAT);
    }
}