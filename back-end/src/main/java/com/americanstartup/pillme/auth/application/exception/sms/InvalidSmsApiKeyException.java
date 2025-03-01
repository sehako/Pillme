package com.americanstartup.pillme.auth.application.exception.sms;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidSmsApiKeyException extends CommonException {
    public InvalidSmsApiKeyException() {
        super(ErrorCode.INVALID_SMS_API_KEY);
    }
}