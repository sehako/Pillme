package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class AlreadyExistPhoneNumberException extends CommonException {
    public AlreadyExistPhoneNumberException() {
        super(ErrorCode.DUPLICATE_PHONE_NUMBER);
    }
}