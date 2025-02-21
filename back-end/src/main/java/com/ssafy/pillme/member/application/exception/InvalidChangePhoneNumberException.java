package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidChangePhoneNumberException extends CommonException {
    public InvalidChangePhoneNumberException() {
        super(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
    }
}
