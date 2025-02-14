package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class NotVerifiedPhoneNumberException extends CommonException {
    public NotVerifiedPhoneNumberException() {
        super(ErrorCode.UNVERIFIED_PHONE_NUMBER);
    }
}