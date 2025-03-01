package com.americanstartup.pillme.management.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class NoMedicationException extends CommonException {
    public NoMedicationException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
