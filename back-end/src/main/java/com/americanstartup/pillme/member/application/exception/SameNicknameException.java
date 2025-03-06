package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class SameNicknameException extends CommonException {
    public SameNicknameException() {
        super(ErrorCode.SAME_NICKNAME);
    }
}
