package com.americanstartup.pillme.auth.application.exception.verification;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidEmailCodeException extends CommonException {
    public InvalidEmailCodeException() {
        super(ErrorCode.INVALID_EMAIL_CODE);
    }
}