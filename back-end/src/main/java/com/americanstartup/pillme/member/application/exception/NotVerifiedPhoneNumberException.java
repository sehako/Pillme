package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NotVerifiedPhoneNumberException extends CommonException {
    public NotVerifiedPhoneNumberException() {
        super(ErrorCode.UNVERIFIED_PHONE_NUMBER);
    }
}