package com.ssafy.pillme.member.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class OAuthUserException extends CommonException {
    public OAuthUserException() {
        super(ErrorCode.OAUTH_USER_CANT_CHANGE_EMAIL);
    }
}
