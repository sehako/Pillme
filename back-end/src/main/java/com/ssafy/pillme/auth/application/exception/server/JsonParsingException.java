package com.ssafy.pillme.auth.application.exception.server;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class JsonParsingException extends CommonException {
    public JsonParsingException() {
        super(ErrorCode.SERVER_ERROR);
    }
}
