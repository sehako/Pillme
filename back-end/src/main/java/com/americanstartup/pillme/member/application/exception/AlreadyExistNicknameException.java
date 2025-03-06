package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class AlreadyExistNicknameException extends CommonException {
    public AlreadyExistNicknameException() {
        super(ErrorCode.DUPLICATE_MEMBER_NICKNAME);
    }
}