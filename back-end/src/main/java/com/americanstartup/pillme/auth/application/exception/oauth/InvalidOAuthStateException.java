package com.americanstartup.pillme.auth.application.exception.oauth;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class InvalidOAuthStateException extends CommonException {
    public InvalidOAuthStateException() {
        super(ErrorCode.INVALID_OAUTH_STATE);
    }
}