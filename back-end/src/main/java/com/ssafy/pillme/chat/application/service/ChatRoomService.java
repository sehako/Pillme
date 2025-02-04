package com.ssafy.pillme.chat.application.service;

import com.ssafy.pillme.chat.application.exception.ChatRoomNotFoundException;
import com.ssafy.pillme.chat.application.response.ChatMessageResponse;
import com.ssafy.pillme.chat.application.response.ChatRoomResponse;
import com.ssafy.pillme.chat.domain.entity.ChatRoom;
import com.ssafy.pillme.chat.infrastructure.repository.ChatRoomRepository;
import com.ssafy.pillme.chat.presentation.request.ChatRoomRequest;
import com.ssafy.pillme.global.code.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoomResponse> getUserChatRoom(Long userId){
        List<ChatRoom> chatRooms =  chatRoomRepository.findByCareUserIdOrUserId(userId, userId);
        if(chatRooms.isEmpty()){
            new ChatRoomNotFoundException(ErrorCode.EMPTY_CHATROOM_ID);
        }
        List<ChatRoomResponse> chatRoomResponses = chatRooms.stream().
                map(ChatRoomResponse::from)
                .toList();
        return chatRoomResponses;
    }

    public ChatRoomResponse getOrCreateChatRoom(ChatRoomRequest chatRoomRequest){
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByCareUserIdAndUserId(chatRoomRequest.careUserId(), chatRoomRequest.userId());
        if(chatRoom.isPresent()){
            return ChatRoomResponse.from(chatRoom.get());
        }
        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.updateChatRoom(chatRoomRequest.careUserId(), chatRoomRequest.userId());
        newChatRoom= chatRoomRepository.save(newChatRoom);
        return ChatRoomResponse.from(newChatRoom);
    }
}
