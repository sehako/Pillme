package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class NoMemberInfoException extends CommonException {
    public NoMemberInfoException() {
        super(ErrorCode.INVALID_MEMBER_INFO);
    }
}