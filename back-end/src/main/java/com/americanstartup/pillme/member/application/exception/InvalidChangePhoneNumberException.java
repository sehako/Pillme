package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidChangePhoneNumberException extends CommonException {
    public InvalidChangePhoneNumberException() {
        super(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
    }
}
