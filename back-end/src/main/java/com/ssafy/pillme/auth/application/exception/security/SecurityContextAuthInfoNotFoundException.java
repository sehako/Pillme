package com.ssafy.pillme.auth.application.exception.security;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class SecurityContextAuthInfoNotFoundException extends CommonException {
    public SecurityContextAuthInfoNotFoundException() {
        super(ErrorCode.SECURITY_CONTEXT_AUTH_INFO_NOT_FOUND);
    }
}