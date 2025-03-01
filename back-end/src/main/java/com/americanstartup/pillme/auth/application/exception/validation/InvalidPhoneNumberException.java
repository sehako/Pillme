package com.americanstartup.pillme.auth.application.exception.validation;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidPhoneNumberException extends CommonException {
    public InvalidPhoneNumberException() {
        super(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
    }
}
