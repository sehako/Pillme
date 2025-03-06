package com.americanstartup.pillme.auth.application.exception.validation;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class DuplicateEmailAddressException extends CommonException {
    public DuplicateEmailAddressException() {
        super(ErrorCode.DUPLICATE_EMAIL_ADDRESS);
    }
}