package com.ssafy.pillme.auth.application.exception.sms;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidSmsApiKeyException extends CommonException {
    public InvalidSmsApiKeyException() {
        super(ErrorCode.INVALID_SMS_API_KEY);
    }
}