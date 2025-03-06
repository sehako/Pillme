package com.americanstartup.pillme.auth.application.exception.security;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class SecurityContextRoleInfoNotFoundException extends CommonException {
    public SecurityContextRoleInfoNotFoundException() {
        super(ErrorCode.SECURITY_CONTEXT_ROLE_INFO_NOT_FOUND);
    }
}