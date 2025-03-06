package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class MismatchedPasswordException extends CommonException {
    public MismatchedPasswordException() {
        super(ErrorCode.INVALID_MEMBER_PASSWORD);
    }
}