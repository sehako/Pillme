package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class NotVerifiedEmailAddressException extends CommonException {
    public NotVerifiedEmailAddressException() {
        super(ErrorCode.UNVERIFIED_EMAIL_ADDRESS);
    }
}