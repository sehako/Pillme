package com.ssafy.pillme.auth.application.exception.validation;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class DuplicateMemberNicknameException extends CommonException {
    public DuplicateMemberNicknameException() {
        super(ErrorCode.DUPLICATE_MEMBER_NICKNAME);
    }
}