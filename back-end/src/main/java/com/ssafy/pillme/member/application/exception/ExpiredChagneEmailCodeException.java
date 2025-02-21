package com.ssafy.pillme.member.application.exception;


import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class ExpiredChagneEmailCodeException extends CommonException {
    public ExpiredChagneEmailCodeException() {
        super(ErrorCode.EXPIRED_EMAIL_CODE);
    }
}