package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidChangeSmsApiKeyException extends CommonException {
    public InvalidChangeSmsApiKeyException() {
        super(ErrorCode.INVALID_SMS_API_KEY);
    }
}