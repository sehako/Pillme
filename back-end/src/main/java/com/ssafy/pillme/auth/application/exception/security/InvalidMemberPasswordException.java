package com.ssafy.pillme.auth.application.exception.security;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidMemberPasswordException extends CommonException {
    public InvalidMemberPasswordException() {
        super(ErrorCode.INVALID_MEMBER_PASSWORD);
    }
}