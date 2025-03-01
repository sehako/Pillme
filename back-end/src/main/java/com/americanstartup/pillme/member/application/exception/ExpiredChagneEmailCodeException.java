package com.americanstartup.pillme.member.application.exception;


import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class ExpiredChagneEmailCodeException extends CommonException {
    public ExpiredChagneEmailCodeException() {
        super(ErrorCode.EXPIRED_EMAIL_CODE);
    }
}