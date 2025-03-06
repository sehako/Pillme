package com.americanstartup.pillme.auth.application.exception.security;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidLoginCredentialsException extends CommonException {
    public InvalidLoginCredentialsException() {
        super(ErrorCode.INVALID_LOGIN_CREDENTIALS);
    }
}