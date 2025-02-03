package com.ssafy.pillme.auth.application.exception.oauth;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class RestrictedOAuthPasswordException extends CommonException {
    public RestrictedOAuthPasswordException() {
        super(ErrorCode.RESTRICTED_OAUTH_PASSWORD);
    }
}