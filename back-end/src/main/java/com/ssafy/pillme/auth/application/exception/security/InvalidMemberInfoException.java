package com.ssafy.pillme.auth.application.exception.security;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidMemberInfoException extends CommonException {
    public InvalidMemberInfoException() {
        super(ErrorCode.INVALID_MEMBER_INFO);
    }
}