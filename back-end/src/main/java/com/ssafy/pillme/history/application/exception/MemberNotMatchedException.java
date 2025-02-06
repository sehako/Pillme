package com.ssafy.pillme.history.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class MemberNotMatchedException extends CommonException {
    public MemberNotMatchedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
