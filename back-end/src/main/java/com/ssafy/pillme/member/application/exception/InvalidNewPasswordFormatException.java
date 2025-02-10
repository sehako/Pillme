package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidNewPasswordFormatException extends CommonException {
    public InvalidNewPasswordFormatException() {
        super(ErrorCode.INVALID_PASSWORD_FORMAT);
    }
}