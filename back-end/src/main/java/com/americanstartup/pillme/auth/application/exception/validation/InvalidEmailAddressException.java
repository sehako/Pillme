package com.americanstartup.pillme.auth.application.exception.validation;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidEmailAddressException extends CommonException {
    public InvalidEmailAddressException() {
        super(ErrorCode.INVALID_EMAIL_ADDRESS_FORMAT);
    }
}
