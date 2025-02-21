package com.ssafy.pillme.dependency.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class DependencyNotFoundException extends CommonException {
    public DependencyNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
