package com.americanstartup.pillme.auth.application.exception.oauth;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class RestrictedOAuthPasswordException extends CommonException {
    public RestrictedOAuthPasswordException() {
        super(ErrorCode.RESTRICTED_OAUTH_PASSWORD);
    }
}