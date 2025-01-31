package com.ssafy.pillme.chat.application.response;

import com.ssafy.pillme.chat.domain.entity.ChatRoom;
import lombok.NoArgsConstructor;

public record ChatRoomResponse(Long id, Long careUserId, Long userId) {
    public static ChatRoomResponse from(ChatRoom chatRoom){
        return new ChatRoomResponse(chatRoom.getId(), chatRoom.getCareUserId(), chatRoom.getUserId());
    }
}
