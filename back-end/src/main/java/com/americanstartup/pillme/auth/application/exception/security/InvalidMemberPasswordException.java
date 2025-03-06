package com.americanstartup.pillme.auth.application.exception.security;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidMemberPasswordException extends CommonException {
    public InvalidMemberPasswordException() {
        super(ErrorCode.INVALID_MEMBER_PASSWORD);
    }
}