package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidChangeEmailCodeException extends CommonException {
    public InvalidChangeEmailCodeException() {
        super(ErrorCode.INVALID_EMAIL_CODE);
    }
}