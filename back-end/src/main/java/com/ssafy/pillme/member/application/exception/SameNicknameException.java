package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class SameNicknameException extends CommonException {
    public SameNicknameException() {
        super(ErrorCode.SAME_NICKNAME);
    }
}
