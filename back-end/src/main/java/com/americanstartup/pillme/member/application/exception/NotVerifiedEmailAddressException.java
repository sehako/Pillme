package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NotVerifiedEmailAddressException extends CommonException {
    public NotVerifiedEmailAddressException() {
        super(ErrorCode.UNVERIFIED_EMAIL_ADDRESS);
    }
}