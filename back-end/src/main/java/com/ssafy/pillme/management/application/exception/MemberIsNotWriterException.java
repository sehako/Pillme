package com.ssafy.pillme.management.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class MemberIsNotWriterException extends CommonException {
    public MemberIsNotWriterException(ErrorCode errorCode) {
        super(errorCode);
    }
}
