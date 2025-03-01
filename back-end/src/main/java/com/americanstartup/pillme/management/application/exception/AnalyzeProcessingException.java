package com.americanstartup.pillme.management.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class AnalyzeProcessingException extends CommonException {
    public AnalyzeProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
