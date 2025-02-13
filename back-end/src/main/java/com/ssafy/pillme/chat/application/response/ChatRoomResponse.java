package com.ssafy.pillme.chat.application.response;

import com.ssafy.pillme.chat.domain.entity.ChatRoom;
import lombok.NoArgsConstructor;

public record ChatRoomResponse(Long chatRoomId, Long sendUserId, Long receiveUserId, String sendUserName, String receiveUserName, int unreadMessageCount, String lastMessage, Long timestamp) {
    public static ChatRoomResponse from(ChatRoom chatRoom, int unreadMessageCount, String lastMessage, Long timestamp){
        return new ChatRoomResponse(chatRoom.getId(), chatRoom.getSendUser().getId(), chatRoom.getReceiveUser().getId(),
                chatRoom.getSendUser().getName(), chatRoom.getReceiveUser().getName(), unreadMessageCount, lastMessage, timestamp);
    }
}
