package com.ssafy.pillme.chat.infrastructure.repository;

import com.ssafy.pillme.chat.domain.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);
}
