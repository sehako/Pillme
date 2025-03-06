package com.americanstartup.pillme.auth.application.exception.verification;


import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class ExpiredEmailCodeException extends CommonException {
    public ExpiredEmailCodeException() {
        super(ErrorCode.EXPIRED_EMAIL_CODE);
    }
}