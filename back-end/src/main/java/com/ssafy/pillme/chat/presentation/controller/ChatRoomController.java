package com.ssafy.pillme.chat.presentation.controller;

import com.ssafy.pillme.chat.application.response.ChatRoomResponse;
import com.ssafy.pillme.chat.application.service.ChatRoomService;
import com.ssafy.pillme.chat.domain.entity.ChatRoom;
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

    @GetMapping("/{userId}")
    public ResponseEntity<JSONResponse<List<ChatRoomResponse>>> getUserChatRooms(@PathVariable Long userId){
        return ResponseEntity.ok(JSONResponse.onSuccess(chatRoomService.getUserChatRoom(userId)));
    }

    @PostMapping
    public ResponseEntity<JSONResponse<ChatRoomResponse>> getOrCreateChatRoom(@RequestBody ChatRoomRequest chatRoomRequest){
        return ResponseEntity.ok(JSONResponse.onSuccess(chatRoomService.getOrCreateChatRoom(chatRoomRequest)));
    }

    @DeleteMapping
    public ResponseEntity<JSONResponse<Void>> deleteChatRoom(@RequestBody ChatRoomRequest chatRoomRequest){
        chatRoomService.deleteChatRoom(chatRoomRequest);
        return  ResponseEntity.ok(JSONResponse.of(SuccessCode.CHATROOM_DELETE_SUCCESS));
    }
}
