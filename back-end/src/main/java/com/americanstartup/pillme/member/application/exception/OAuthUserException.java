package com.americanstartup.pillme.member.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class OAuthUserException extends CommonException {
    public OAuthUserException() {
        super(ErrorCode.OAUTH_USER_CANT_CHANGE_EMAIL);
    }
}
