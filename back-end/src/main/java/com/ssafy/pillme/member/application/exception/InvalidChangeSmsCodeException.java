package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidChangeSmsCodeException extends CommonException {
    public InvalidChangeSmsCodeException() {
        super(ErrorCode.INVALID_SMS_CODE);
    }
}