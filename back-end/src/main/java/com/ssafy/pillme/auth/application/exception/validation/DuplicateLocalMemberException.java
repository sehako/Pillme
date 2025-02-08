package com.ssafy.pillme.auth.application.exception.validation;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class DuplicateLocalMemberException extends CommonException {
    public DuplicateLocalMemberException() {
        super(ErrorCode.DUPLICATE_LOCAL_MEMBER);
    }
}
