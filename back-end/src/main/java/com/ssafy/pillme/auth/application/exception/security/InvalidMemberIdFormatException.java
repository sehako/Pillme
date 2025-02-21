package com.ssafy.pillme.auth.application.exception.security;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidMemberIdFormatException extends CommonException {
    public InvalidMemberIdFormatException() {
        super(ErrorCode.INVALID_MEMBER_ID_FORMAT);
    }
}