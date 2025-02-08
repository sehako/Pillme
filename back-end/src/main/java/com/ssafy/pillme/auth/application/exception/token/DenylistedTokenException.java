package com.ssafy.pillme.auth.application.exception.token;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class DenylistedTokenException extends CommonException {
    public DenylistedTokenException() {
        super(ErrorCode.DENYLISTED_TOKEN);
    }
}
