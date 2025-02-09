package com.ssafy.pillme.chat.presentation.controller;

import com.ssafy.pillme.auth.annotation.Auth;
import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.chat.application.response.ChatRoomResponse;
import com.ssafy.pillme.chat.application.service.ChatRedisService;
import com.ssafy.pillme.chat.application.service.ChatRoomService;
import com.ssafy.pillme.chat.presentation.request.ChatRoomRequest;
import com.ssafy.pillme.global.code.SuccessCode;
import com.ssafy.pillme.global.response.JSONResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping("/api/v1/chat/rooms")
@AllArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRedisService chatRedisService;

    @GetMapping("/{userId}")
    public ResponseEntity<JSONResponse<List<ChatRoomResponse>>> getUserChatRooms(@PathVariable Long userId){
        return ResponseEntity.ok(JSONResponse.onSuccess(chatRoomService.getUserChatRoom(userId)));
    }

    @PostMapping
    public ResponseEntity<JSONResponse<ChatRoomResponse>> getOrCreateChatRoom(@RequestBody ChatRoomRequest chatRoomRequest){
        return ResponseEntity.ok(JSONResponse.onSuccess(chatRoomService.getOrCreateChatRoom(chatRoomRequest)));
    }

    @DeleteMapping("/{chatRoom}")
    public ResponseEntity<JSONResponse<Void>> deleteChatRoom(@PathVariable Long chatRoom){
        chatRoomService.deleteChatRoom(chatRoom);
        return ResponseEntity.ok(JSONResponse.of(SuccessCode.CHATROOM_DELETE_SUCCESS));
    }

    @PostMapping("/{chatRoomId}/{userId}")
    public ResponseEntity<JSONResponse<Void>> leaveChatRoom(@PathVariable Long chatRoomId, @PathVariable Long userId){
        chatRedisService.leaveChatRoom(chatRoomId, userId);
        return ResponseEntity.ok(JSONResponse.of(SuccessCode.CHATROOM_LEAVE_SUCCESS));
    }

}
