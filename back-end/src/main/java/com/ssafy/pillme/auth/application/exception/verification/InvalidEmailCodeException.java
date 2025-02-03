package com.ssafy.pillme.auth.application.exception.verification;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidEmailCodeException extends CommonException {
    public InvalidEmailCodeException() {
        super(ErrorCode.INVALID_EMAIL_CODE);
    }
}