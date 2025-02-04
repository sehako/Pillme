package com.ssafy.pillme.management.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidRegistrationTypeException extends CommonException {
    public InvalidRegistrationTypeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
