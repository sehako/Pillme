package com.ssafy.pillme.auth.application.exception.validation;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class MismatchedPhoneNumberException extends CommonException {
    public MismatchedPhoneNumberException() {
        super(ErrorCode.MISMATCHED_PHONE_NUMBER);
    }
}