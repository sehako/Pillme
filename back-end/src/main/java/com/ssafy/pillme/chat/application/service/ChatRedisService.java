package com.ssafy.pillme.chat.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String CHAT_ROOM_PREFIX = "chat_room:";

    //채팅방 입장 시 사용자 등록
    public void enterChatRoom(Long chatRoomId, Long userId) {
        redisTemplate.opsForSet().add(CHAT_ROOM_PREFIX + chatRoomId, userId.toString());
    }

    //채팅방 퇴장 시 사용자 제거
    public void leaveChatRoom(Long chatRoomId, Long userId) {
        redisTemplate.opsForSet().remove(CHAT_ROOM_PREFIX + chatRoomId, userId.toString());
    }

    public boolean checkMember(Long chatRoomId, Long receiverId){
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(CHAT_ROOM_PREFIX + chatRoomId, receiverId.toString()));
    }
}
