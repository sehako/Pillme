package com.ssafy.pillme.auth.application.exception.validation;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class DuplicateEmailAddressException extends CommonException {
    public DuplicateEmailAddressException() {
        super(ErrorCode.DUPLICATE_EMAIL_ADDRESS);
    }
}