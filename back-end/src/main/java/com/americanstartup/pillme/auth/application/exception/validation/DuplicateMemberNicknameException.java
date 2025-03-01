package com.americanstartup.pillme.auth.application.exception.validation;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class DuplicateMemberNicknameException extends CommonException {
    public DuplicateMemberNicknameException() {
        super(ErrorCode.DUPLICATE_MEMBER_NICKNAME);
    }
}