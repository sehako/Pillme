package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class AlreadyExistPhoneNumberException extends CommonException {
    public AlreadyExistPhoneNumberException() {
        super(ErrorCode.DUPLICATE_PHONE_NUMBER);
    }
}