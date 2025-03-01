package com.americanstartup.pillme.dependency.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class DependencyNotFoundException extends CommonException {
    public DependencyNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
