package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class SameEmailException extends CommonException {
    public SameEmailException() {
        super(ErrorCode.SAME_EMAIL_ADDRESS);
    }
}
