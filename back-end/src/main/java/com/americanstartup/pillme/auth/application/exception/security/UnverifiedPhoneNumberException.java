package com.americanstartup.pillme.auth.application.exception.security;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class UnverifiedPhoneNumberException extends CommonException {
    public UnverifiedPhoneNumberException() {
        super(ErrorCode.UNVERIFIED_PHONE_NUMBER);
    }
}