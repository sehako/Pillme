package com.americanstartup.pillme.auth.application.exception.security;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidPasswordFormatException extends CommonException {
    public InvalidPasswordFormatException() {
        super(ErrorCode.INVALID_PASSWORD_FORMAT);
    }
}