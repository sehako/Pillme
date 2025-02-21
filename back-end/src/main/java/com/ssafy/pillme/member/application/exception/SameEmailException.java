package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class SameEmailException extends CommonException {
    public SameEmailException() {
        super(ErrorCode.SAME_EMAIL_ADDRESS);
    }
}
