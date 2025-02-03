package com.ssafy.pillme.auth.application.exception.security;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class UnverifiedEmailAddressException extends CommonException {
    public UnverifiedEmailAddressException() {
        super(ErrorCode.UNVERIFIED_EMAIL_ADDRESS);
    }
}