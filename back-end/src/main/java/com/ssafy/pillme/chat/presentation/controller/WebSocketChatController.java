package com.ssafy.pillme.chat.presentation.controller;

import com.ssafy.pillme.auth.annotation.Auth;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.auth.util.JwtUtil;
import com.ssafy.pillme.chat.application.response.ChatRoomUpdateResponse;
import com.ssafy.pillme.chat.application.service.ChatMessageService;
import com.ssafy.pillme.chat.application.service.ChatRoomService;
import com.ssafy.pillme.chat.domain.entity.ChatMessage;
import com.ssafy.pillme.chat.infrastructure.repository.ChatMessageRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Date;
import java.util.Objects;

@Controller
@AllArgsConstructor
public class WebSocketChatController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;

    @MessageMapping("/chat.{chatRoomId}")
    @SendTo("/subscribe/chat.{chatRoomId}")
    public ChatMessage sendMessage(ChatMessage message, @Header("Authorization") String accessToken){
        Long userId = jwtUtil.getUserIdFromToken(accessToken);
        chatMessageService.saveMessage(message);
        Long receiveId = Objects.equals(userId, message.getSenderId()) ? message.getReceiverId() : message.getSenderId();
        int unreadCount = chatRoomService.countNotReadMessages(message.getChatRoomId(), receiveId);
        String lastMessage = chatRoomService.getLastChatMessage(message.getChatRoomId());
        messagingTemplate.convertAndSend("/subscribe/chat/list/" + receiveId,
                new ChatRoomUpdateResponse(message.getChatRoomId(), unreadCount, new Date().getTime(), lastMessage));
        return message;
    }
}
