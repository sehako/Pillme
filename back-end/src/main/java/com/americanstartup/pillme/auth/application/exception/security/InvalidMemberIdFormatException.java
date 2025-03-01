package com.americanstartup.pillme.auth.application.exception.security;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidMemberIdFormatException extends CommonException {
    public InvalidMemberIdFormatException() {
        super(ErrorCode.INVALID_MEMBER_ID_FORMAT);
    }
}