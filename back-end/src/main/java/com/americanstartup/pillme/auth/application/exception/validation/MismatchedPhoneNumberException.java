package com.americanstartup.pillme.auth.application.exception.validation;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class MismatchedPhoneNumberException extends CommonException {
    public MismatchedPhoneNumberException() {
        super(ErrorCode.MISMATCHED_PHONE_NUMBER);
    }
}