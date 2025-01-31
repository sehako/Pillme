package com.ssafy.pillme.chat.application.service;

import com.ssafy.pillme.chat.application.response.ChatMessageResponse;
import com.ssafy.pillme.chat.domain.entity.ChatMessage;
import com.ssafy.pillme.chat.infrastructure.repository.ChatMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageResponse> getChatMessages(Long chatRoomId){
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
        List<ChatMessageResponse> responses = chatMessages.
                stream()
                .map(ChatMessageResponse::from)
                .toList();
        return responses;
    }

    public ChatMessage saveMessage(ChatMessage chatMessage){
        return chatMessageRepository.save(chatMessage);
    }
}
