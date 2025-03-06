package com.americanstartup.pillme.dependency.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class DuplicateDependencyException extends CommonException {
    public DuplicateDependencyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
