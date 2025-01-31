package com.ssafy.pillme.chat.presentation.controller;

import com.ssafy.pillme.chat.application.service.ChatMessageService;
import com.ssafy.pillme.chat.domain.entity.ChatMessage;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WebSocketChatController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.{chatRoomId}")
    @SendTo("/subscribe/chat.{chatRoomId}")
    public ChatMessage sendMessage(ChatMessage message){
        chatMessageService.saveMessage(message);
        return message;
    }
}
