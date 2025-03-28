package com.americanstartup.pillme.chat.presentation.controller;

import com.americanstartup.pillme.auth.application.service.AuthService;
import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.auth.util.JwtUtil;
import com.americanstartup.pillme.chat.application.response.ChatRoomUpdateResponse;
import com.americanstartup.pillme.chat.application.service.ChatMessageService;
import com.americanstartup.pillme.chat.application.service.ChatRoomService;
import com.americanstartup.pillme.chat.domain.entity.ChatMessage;
import com.americanstartup.pillme.notification.application.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.Objects;

@Controller
@AllArgsConstructor
public class WebSocketChatController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    //실시간 메세지 저장 및 마지막 메세지 조회, 안 읽은 메세지 조회를 통해 상대방의 채팅 리스트 실시간 업데이트
    @MessageMapping("/chat.{chatRoomId}")
    @SendTo("/subscribe/chat.{chatRoomId}")
    public ChatMessage sendMessage(ChatMessage message, @Header("Authorization") String accessToken){
        Long userId = jwtUtil.getUserIdFromToken(accessToken);
        Member sender = authService.findById(userId);
        ChatMessage chatMessage = chatMessageService.saveMessage(message);
        Long receiveId = Objects.equals(userId, message.getSenderId()) ? message.getReceiverId() : message.getSenderId();
        Member receiver = authService.findById(receiveId);
        int unreadCount = chatRoomService.countNotReadMessages(message.getChatRoomId(), receiveId);
        String lastMessage = chatRoomService.getLastChatMessage(message.getChatRoomId());

        if(!chatMessage.isRead()){
            notificationService.sendChatNotification(message.getChatRoomId(), sender, receiver, message.getMessage(), message.getTimestamp());
        }

        messagingTemplate.convertAndSend("/subscribe/chat/list/" + receiveId,
                new ChatRoomUpdateResponse(message.getChatRoomId(), unreadCount, new Date().getTime(), lastMessage));
        return message;
    }
}
