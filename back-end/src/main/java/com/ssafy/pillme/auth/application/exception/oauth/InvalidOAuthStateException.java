package com.ssafy.pillme.auth.application.exception.oauth;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class InvalidOAuthStateException extends CommonException {
    public InvalidOAuthStateException() {
        super(ErrorCode.INVALID_OAUTH_STATE);
    }
}