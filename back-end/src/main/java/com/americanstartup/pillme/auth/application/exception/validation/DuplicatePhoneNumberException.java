package com.americanstartup.pillme.auth.application.exception.validation;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class DuplicatePhoneNumberException extends CommonException {
    public DuplicatePhoneNumberException() {
        super(ErrorCode.DUPLICATE_PHONE_NUMBER);
    }
}