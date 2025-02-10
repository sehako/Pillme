package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class AlreadyExistEmailAddressException extends CommonException {
    public AlreadyExistEmailAddressException() {
        super(ErrorCode.DUPLICATE_EMAIL_ADDRESS);
    }
}