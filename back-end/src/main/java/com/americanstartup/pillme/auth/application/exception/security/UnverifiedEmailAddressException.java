package com.americanstartup.pillme.auth.application.exception.security;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class UnverifiedEmailAddressException extends CommonException {
    public UnverifiedEmailAddressException() {
        super(ErrorCode.UNVERIFIED_EMAIL_ADDRESS);
    }
}