package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class ExpiredChangeSmsCodeException extends CommonException {
    public ExpiredChangeSmsCodeException() {
        super(ErrorCode.EXPIRED_SMS_CODE);
    }
}