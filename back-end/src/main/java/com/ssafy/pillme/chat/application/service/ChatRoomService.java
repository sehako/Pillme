package com.ssafy.pillme.chat.application.service;

import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.chat.application.exception.ChatRoomNotFoundException;
import com.ssafy.pillme.chat.application.response.ChatMessageResponse;
import com.ssafy.pillme.chat.application.response.ChatRoomResponse;
import com.ssafy.pillme.chat.domain.entity.ChatRoom;
import com.ssafy.pillme.chat.infrastructure.repository.ChatMessageRepository;
import com.ssafy.pillme.chat.infrastructure.repository.ChatRoomRepository;
import com.ssafy.pillme.chat.presentation.request.ChatRoomRequest;
import com.ssafy.pillme.global.code.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AuthService authService;
    private final ChatRedisService chatRedisService;


    public List<ChatRoomResponse> getUserChatRoom(Long userId){
        Member user = authService.findById(userId);
        List<ChatRoom> chatRooms =  chatRoomRepository.findBySendUserOrReceiveUser(user, user);
        if(chatRooms.isEmpty()){
            throw new ChatRoomNotFoundException(ErrorCode.EMPTY_CHATROOM_ID);
        }
        return chatRooms.stream()
                .map(chatRoom -> ChatRoomResponse.from(
                        chatRoom,
                        chatMessageRepository.countByChatRoomIdAndSenderIdNotAndReadFalse(chatRoom.getId(), userId)
                ))
                .collect(Collectors.toList());
    }

    public ChatRoomResponse getOrCreateChatRoom(ChatRoomRequest chatRoomRequest){
        Member careUser = authService.findById(chatRoomRequest.sendUserId());
        Member user = authService.findById(chatRoomRequest.receiveUserId());
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByUsers(careUser, user);
        if(chatRoom.isPresent()){
            chatRedisService.enterChatRoom(chatRoom.get().getId(), chatRoomRequest.sendUserId());   //myId로 바꿔야됨
            return ChatRoomResponse.from(chatRoom.get(),0);
        }
        ChatRoom newChatRoom = new ChatRoom();

        newChatRoom.updateChatRoom(careUser, user);
        newChatRoom= chatRoomRepository.save(newChatRoom);
        chatRedisService.enterChatRoom(newChatRoom.getId(), chatRoomRequest.sendUserId());   //myId로 바꿔야됨
        return ChatRoomResponse.from(newChatRoom,0);
    }

    public void deleteChatRoom(Long chatRoom){
        if(chatRoomRepository.existsById(chatRoom)){
            chatRoomRepository.deleteById(chatRoom);
        } else{
            throw new ChatRoomNotFoundException(ErrorCode.EMPTY_CHATROOM_ID);
        }
    }
}
