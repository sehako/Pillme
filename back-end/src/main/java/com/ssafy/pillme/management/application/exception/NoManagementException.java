package com.ssafy.pillme.management.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class NoManagementException extends CommonException {
    public NoManagementException(ErrorCode errorCode) {
        super(errorCode);
    }
}
