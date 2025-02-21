package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidPasswordException extends CommonException {
    public InvalidPasswordException() {
        super(ErrorCode.MISMATCHED_CURRENT_PASSWORD);
    }
}
