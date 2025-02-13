package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class MismatchedPasswordException extends CommonException {
    public MismatchedPasswordException() {
        super(ErrorCode.INVALID_MEMBER_PASSWORD);
    }
}