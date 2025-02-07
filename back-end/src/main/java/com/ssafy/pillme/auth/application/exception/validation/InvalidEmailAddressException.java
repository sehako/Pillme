package com.ssafy.pillme.auth.application.exception.validation;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidEmailAddressException extends CommonException {
    public InvalidEmailAddressException() {
        super(ErrorCode.INVALID_EMAIL_ADDRESS_FORMAT);
    }
}
