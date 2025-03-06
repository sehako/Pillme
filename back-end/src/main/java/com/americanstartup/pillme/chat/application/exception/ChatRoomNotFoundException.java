package com.americanstartup.pillme.chat.application.exception;

import com.americanstartup.pillme.global.code.ErrorCode;
import com.americanstartup.pillme.global.exception.CommonException;

public class ChatRoomNotFoundException extends CommonException {
    public ChatRoomNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
