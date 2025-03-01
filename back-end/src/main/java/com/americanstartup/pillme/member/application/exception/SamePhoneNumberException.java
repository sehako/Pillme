package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class SamePhoneNumberException extends CommonException {
    public SamePhoneNumberException() {
        super(ErrorCode.SAME_PHONE_NUMBER);
    }
}
