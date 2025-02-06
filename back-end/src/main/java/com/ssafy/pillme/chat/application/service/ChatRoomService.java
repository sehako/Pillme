package com.ssafy.pillme.chat.application.service;

import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.entity.Member;
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
    private final AuthService authService;

    public List<ChatRoomResponse> getUserChatRoom(Long userId){
        Member user = authService.findById(userId);
        List<ChatRoom> chatRooms =  chatRoomRepository.findByCareUserOrUser(user, user);
        if(chatRooms.isEmpty()){
            throw new ChatRoomNotFoundException(ErrorCode.EMPTY_CHATROOM_ID);
        }
        return chatRooms.stream().
                map(ChatRoomResponse::from)
                .toList();
    }

    public ChatRoomResponse getOrCreateChatRoom(ChatRoomRequest chatRoomRequest){
        Member careUser = authService.findById(chatRoomRequest.careUserId());
        Member user = authService.findById(chatRoomRequest.userId());
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByCareUserAndUser(careUser, user);
        if(chatRoom.isPresent()){
            return ChatRoomResponse.from(chatRoom.get());
        }
        ChatRoom newChatRoom = new ChatRoom();

        newChatRoom.updateChatRoom(careUser, careUser);
        newChatRoom= chatRoomRepository.save(newChatRoom);
        return ChatRoomResponse.from(newChatRoom);
    }

    public void deleteChatRoom(ChatRoomRequest chatRoomRequest){
        Member careUser = authService.findById(chatRoomRequest.careUserId());
        Member user = authService.findById(chatRoomRequest.userId());

        chatRoomRepository.deleteByCareUserAndUser(careUser, user);
    }
}
