package com.ssafy.pillme.auth.application.exception.verification;


import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class ExpiredEmailCodeException extends CommonException {
    public ExpiredEmailCodeException() {
        super(ErrorCode.EXPIRED_EMAIL_CODE);
    }
}