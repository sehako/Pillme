package com.americanstartup.pillme.auth.application.exception.server;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class JsonParsingException extends CommonException {
    public JsonParsingException() {
        super(ErrorCode.SERVER_ERROR);
    }
}
