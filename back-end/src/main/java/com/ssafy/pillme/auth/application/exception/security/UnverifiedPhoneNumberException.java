package com.ssafy.pillme.auth.application.exception.security;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class UnverifiedPhoneNumberException extends CommonException {
    public UnverifiedPhoneNumberException() {
        super(ErrorCode.UNVERIFIED_PHONE_NUMBER);
    }
}