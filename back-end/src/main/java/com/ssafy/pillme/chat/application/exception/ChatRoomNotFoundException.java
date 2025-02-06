package com.ssafy.pillme.chat.application.exception;

import com.ssafy.pillme.global.code.ErrorCode;
import com.ssafy.pillme.global.exception.CommonException;

public class ChatRoomNotFoundException extends CommonException {
    public ChatRoomNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
