package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NoMemberInfoException extends CommonException {
    public NoMemberInfoException() {
        super(ErrorCode.INVALID_MEMBER_INFO);
    }
}