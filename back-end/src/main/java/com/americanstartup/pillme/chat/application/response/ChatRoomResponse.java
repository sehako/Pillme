package com.americanstartup.pillme.chat.application.response;

import com.americanstartup.pillme.chat.domain.entity.ChatRoom;

public record ChatRoomResponse(Long chatRoomId, Long sendUserId, Long receiveUserId, String sendUserName, String receiveUserName, int unreadMessageCount, String lastMessage, Long timestamp) {
    public static ChatRoomResponse from(ChatRoom chatRoom, int unreadMessageCount, String lastMessage, Long timestamp){
        return new ChatRoomResponse(chatRoom.getId(), chatRoom.getSendUser().getId(), chatRoom.getReceiveUser().getId(),
                chatRoom.getSendUser().getName(), chatRoom.getReceiveUser().getName(), unreadMessageCount, lastMessage, timestamp);
    }
}
