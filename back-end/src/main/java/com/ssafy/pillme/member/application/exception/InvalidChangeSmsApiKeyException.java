package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidChangeSmsApiKeyException extends CommonException {
    public InvalidChangeSmsApiKeyException() {
        super(ErrorCode.INVALID_SMS_API_KEY);
    }
}