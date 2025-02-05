package com.ssafy.pillme.auth.application.exception.validation;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class DuplicatePhoneNumberException extends CommonException {
    public DuplicatePhoneNumberException() {
        super(ErrorCode.DUPLICATE_PHONE_NUMBER);
    }
}