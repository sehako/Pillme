package com.americanstartup.pillme.auth.application.exception.token;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class DenylistedTokenException extends CommonException {
    public DenylistedTokenException() {
        super(ErrorCode.DENYLISTED_TOKEN);
    }
}
