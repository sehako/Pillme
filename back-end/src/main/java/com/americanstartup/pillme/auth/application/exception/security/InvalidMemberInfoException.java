package com.americanstartup.pillme.auth.application.exception.security;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidMemberInfoException extends CommonException {
    public InvalidMemberInfoException() {
        super(ErrorCode.INVALID_MEMBER_INFO);
    }
}