package com.ssafy.pillme.history.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class HistoryNotFoundException extends CommonException {
    public HistoryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
