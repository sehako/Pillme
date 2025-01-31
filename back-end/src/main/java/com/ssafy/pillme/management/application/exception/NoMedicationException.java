package com.ssafy.pillme.management.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class NoMedicationException extends CommonException {
    public NoMedicationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
