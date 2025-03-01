package com.americanstartup.pillme.chat.application.service;

import com.americanstartup.pillme.chat.application.response.ChatMessageResponse;
import com.americanstartup.pillme.chat.domain.entity.ChatMessage;
import com.americanstartup.pillme.chat.infrastructure.repository.ChatMessageRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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
        chatRedisService.enterChatRoom(chatRoomId, userId); //사용자 채팅방 입장 처리
        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomIdAndReceiverIdAndReadFalse(chatRoomId, userId);
        if (!unreadMessages.isEmpty()) {
            for (ChatMessage message : unreadMessages) {
                message.markAsRead();
            }
            chatMessageRepository.saveAll(unreadMessages);
        }
    }
}
