package com.americanstartup.pillme.auth.application.exception.token;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidResetTokenException extends CommonException {
    public InvalidResetTokenException() {
        super(ErrorCode.INVALID_RESET_TOKEN);
    }
}