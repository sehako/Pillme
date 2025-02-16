package com.ssafy.pillme.dependency.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class SelfDependencyRequestException extends CommonException {
    public SelfDependencyRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
