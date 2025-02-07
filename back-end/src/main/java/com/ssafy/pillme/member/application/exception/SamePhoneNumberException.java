package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class SamePhoneNumberException extends CommonException {
    public SamePhoneNumberException() {
        super(ErrorCode.SAME_PHONE_NUMBER);
    }
}
