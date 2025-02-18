package com.ssafy.pillme.chat.application.service;

import com.ssafy.pillme.auth.application.service.AuthService;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.chat.application.exception.ChatRoomNotFoundException;
import com.ssafy.pillme.chat.application.response.ChatMessageResponse;
import com.ssafy.pillme.chat.application.response.ChatRoomResponse;
import com.ssafy.pillme.chat.domain.entity.ChatMessage;
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
import java.util.Date;
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


    public List<ChatRoomResponse> getUserChatRoom(Member user){
        List<ChatRoom> chatRooms =  chatRoomRepository.findBySendUserOrReceiveUser(user, user);
        if(chatRooms.isEmpty()){
            throw new ChatRoomNotFoundException(ErrorCode.EMPTY_CHATROOM_ID);
        }
        return chatRooms.stream()
                .map(chatRoom -> {
                    Optional<ChatMessage> lastMessage = chatMessageRepository.findTopByChatRoomIdOrderByTimestampDesc(chatRoom.getId());

                    return ChatRoomResponse.from(
                            chatRoom,
                            chatMessageRepository.countByChatRoomIdAndSenderIdNotAndReadFalse(chatRoom.getId(), user.getId()),
                            lastMessage.map(ChatMessage::getMessage).orElse(""),
                            lastMessage.map(ChatMessage::getTimestamp).orElse(null)
                    );
                })
                .collect(Collectors.toList());
    }

    public ChatRoomResponse getOrCreateChatRoom(ChatRoomRequest chatRoomRequest, Long myId){
        Member sendUser = authService.findById(chatRoomRequest.sendUserId());
        Member receiveUser = authService.findById(chatRoomRequest.receiveUserId());
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByUsers(sendUser, receiveUser);
        if(chatRoom.isPresent()){
            chatRedisService.enterChatRoom(chatRoom.get().getId(), myId);   //myId로 바꿔야됨
            return ChatRoomResponse.from(chatRoom.get(),0,"", new Date().getTime());
        }
        ChatRoom newChatRoom = new ChatRoom();

        newChatRoom.updateChatRoom(sendUser, receiveUser);
        newChatRoom= chatRoomRepository.save(newChatRoom);
        chatRedisService.enterChatRoom(newChatRoom.getId(), myId);   //myId로 바꿔야됨
        return ChatRoomResponse.from(newChatRoom,0,"",new Date().getTime());
    }

    public void createChatRoom(Member protector, Member dependent){
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByUsers(protector, dependent); //채팅방이 이전에 생성 되었는지 확인
        if(chatRoom.isPresent()){   //있을 경우는 리턴
            return;
        }
        ChatRoom newChatRoom = new ChatRoom();  //없을 경우 새롭게 채팅방 생성
        newChatRoom.updateChatRoom(protector, dependent);
        chatRoomRepository.save(newChatRoom);
    }

    public String getLastChatMessage(Long chatRoomId){
        return chatMessageRepository.findTopByChatRoomIdOrderByTimestampDesc(chatRoomId).map(ChatMessage::getMessage).orElse("");
    }

    public int countNotReadMessages(Long chatRoomId, Long userId){
        return chatMessageRepository.countByChatRoomIdAndReceiverIdAndReadFalse(chatRoomId, userId);
    }

    public void deleteChatRoom(Long chatRoom){
        if(chatRoomRepository.existsById(chatRoom)){
            chatRoomRepository.deleteById(chatRoom);
        } else{
            throw new ChatRoomNotFoundException(ErrorCode.EMPTY_CHATROOM_ID);
        }
    }

    public void deleteChatRoom(Member sendUser, Member receiveUser) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByUsers(sendUser, receiveUser);
        if (chatRoom.isPresent()) {
            chatRoomRepository.delete(chatRoom.get());
        } else {
            throw new ChatRoomNotFoundException(ErrorCode.EMPTY_CHATROOM_ID);
        }
    }
}
