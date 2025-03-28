package com.americanstartup.pillme.chat.infrastructure.repository;

import com.americanstartup.pillme.chat.domain.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);

    Optional<ChatMessage> findTopByChatRoomIdOrderByTimestampDesc(Long chatRoomId);

    //채팅방에 상대방이 보낸 읽지않은 메세지 개수
    int countByChatRoomIdAndSenderIdNotAndReadFalse(Long chatRoomId, Long userId);


    int countByChatRoomIdAndReceiverIdAndReadFalse(Long chatRoomId, Long userId);

    //읽지않은 채팅메세지
    List<ChatMessage> findByChatRoomIdAndReceiverIdAndReadFalse(Long chatRoomId, Long userId);
}
