package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidPasswordException extends CommonException {
    public InvalidPasswordException() {
        super(ErrorCode.MISMATCHED_CURRENT_PASSWORD);
    }
}
