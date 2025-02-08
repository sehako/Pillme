package com.ssafy.pillme.chat.presentation.controller;

import com.ssafy.pillme.auth.annotation.Auth;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.chat.application.response.ChatMessageResponse;
import com.ssafy.pillme.chat.application.service.ChatMessageService;
import com.ssafy.pillme.chat.domain.entity.ChatMessage;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Controller
@RequestMapping("/api/v1/chat")
@RestController
@AllArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<JSONResponse<List<ChatMessageResponse>>> getChatMessages(@PathVariable Long chatRoomId){
        return ResponseEntity.ok(JSONResponse.onSuccess(chatMessageService.getChatMessages(chatRoomId)));
    }
}
