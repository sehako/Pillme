package com.ssafy.pillme.auth.application.exception.validation;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidPhoneNumberException extends CommonException {
    public InvalidPhoneNumberException() {
        super(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
    }
}
