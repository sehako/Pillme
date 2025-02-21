package com.ssafy.pillme.chat.application.response;

import com.ssafy.pillme.chat.domain.entity.ChatMessage;

public record ChatMessageResponse(String id, Long chatRoomId, Long senderId,
                                  String message, Long timestamp) {
    public static ChatMessageResponse from(ChatMessage chatMessage){
        return new ChatMessageResponse(chatMessage.getId(), chatMessage.getChatRoomId(), chatMessage.getSenderId(),
                chatMessage.getMessage(), chatMessage.getTimestamp());
    }
}
