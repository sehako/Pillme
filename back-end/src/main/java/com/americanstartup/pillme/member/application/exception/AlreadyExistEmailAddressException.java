package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class AlreadyExistEmailAddressException extends CommonException {
    public AlreadyExistEmailAddressException() {
        super(ErrorCode.DUPLICATE_EMAIL_ADDRESS);
    }
}