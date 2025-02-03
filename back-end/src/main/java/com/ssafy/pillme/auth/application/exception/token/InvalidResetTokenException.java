package com.ssafy.pillme.auth.application.exception.token;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidResetTokenException extends CommonException {
    public InvalidResetTokenException() {
        super(ErrorCode.INVALID_RESET_TOKEN);
    }
}