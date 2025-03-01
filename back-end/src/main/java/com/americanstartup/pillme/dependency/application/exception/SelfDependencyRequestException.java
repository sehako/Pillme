package com.americanstartup.pillme.dependency.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class SelfDependencyRequestException extends CommonException {
    public SelfDependencyRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
