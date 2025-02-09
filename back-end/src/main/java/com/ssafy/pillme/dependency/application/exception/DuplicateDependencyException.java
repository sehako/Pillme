package com.ssafy.pillme.dependency.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class DuplicateDependencyException extends CommonException {
    public DuplicateDependencyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
