package com.americanstartup.pillme.history.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class MemberNotMatchedException extends CommonException {
    public MemberNotMatchedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
