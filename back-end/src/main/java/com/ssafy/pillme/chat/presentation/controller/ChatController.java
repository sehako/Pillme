package com.ssafy.pillme.chat.presentation.controller;

import com.ssafy.pillme.auth.annotation.Auth;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.chat.application.response.ChatMessageResponse;
import com.ssafy.pillme.chat.application.service.ChatMessageService;
import com.ssafy.pillme.chat.domain.entity.ChatMessage;
import com.ssafy.pillme.chat.presentation.request.ChatReadRequest;
import com.ssafy.pillme.global.code.SuccessCode;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/api/v1/chat")
@RestController
@AllArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    //채팅방에 해당하는 메세지 불러오기
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<JSONResponse<List<ChatMessageResponse>>> getChatMessages(@PathVariable Long chatRoomId){
        return ResponseEntity.ok(JSONResponse.onSuccess(chatMessageService.getChatMessages(chatRoomId)));
    }
    //채팅방에 유저가 들어올 시 본인에게 온 메세지 중 안 읽은 메세지 읽음으로 처리
    @PostMapping("/read/{chatRoomId}")
    public ResponseEntity<JSONResponse<Void>> readChatRoom(@PathVariable Long chatRoomId, @Auth Member member){
        chatMessageService.markMessageAsRead(chatRoomId, member.getId());
        return ResponseEntity.ok(JSONResponse.of(SuccessCode.REQUEST_SUCCESS));
    }
}
