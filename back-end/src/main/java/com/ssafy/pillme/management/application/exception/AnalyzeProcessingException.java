package com.ssafy.pillme.management.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class AnalyzeProcessingException extends CommonException {
    public AnalyzeProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
