package com.ssafy.pillme.auth.application.exception.security;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidLoginCredentialsException extends CommonException {
    public InvalidLoginCredentialsException() {
        super(ErrorCode.INVALID_LOGIN_CREDENTIALS);
    }
}