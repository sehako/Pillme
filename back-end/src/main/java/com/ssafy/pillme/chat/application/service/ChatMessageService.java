package com.ssafy.pillme.chat.application.service;

import com.ssafy.pillme.chat.application.exception.MarkMessageAsReadException;
import com.ssafy.pillme.chat.application.response.ChatMessageResponse;
import com.ssafy.pillme.chat.domain.entity.ChatMessage;
import com.ssafy.pillme.chat.infrastructure.repository.ChatMessageRepository;
import com.ssafy.pillme.global.code.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRedisService chatRedisService;

    public List<ChatMessageResponse> getChatMessages(Long chatRoomId){
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
        return chatMessages.
                stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    public ChatMessage saveMessage(ChatMessage chatMessage){
        Long chatRoomId = chatMessage.getChatRoomId();
        Long receiverId = chatMessage.getReceiverId();


        //채팅 상대가 채팅방에 있는지 확인
        Boolean isOnline = chatRedisService.checkMember(chatRoomId, receiverId);
        if(Boolean.TRUE.equals(isOnline)){
            chatMessage.markAsRead();
        }

        return chatMessageRepository.save(chatMessage);
    }

    public void markMessageAsRead(Long chatRoomId, Long userId){
        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomIdAndReceiverIdAndReadFalse(chatRoomId, userId);
        if (!unreadMessages.isEmpty()) {
            for (ChatMessage message : unreadMessages) {
                message.markAsRead();
            }
            chatMessageRepository.saveAll(unreadMessages);
        }
    }
}
