package com.americanstartup.pillme.chat.presentation.controller;

import com.americanstartup.pillme.auth.annotation.Auth;
import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.chat.application.response.ChatRoomResponse;
import com.americanstartup.pillme.chat.application.service.ChatRedisService;
import com.americanstartup.pillme.chat.application.service.ChatRoomService;
import com.americanstartup.pillme.chat.presentation.request.ChatRoomRequest;
import com.americanstartup.pillme.global.code.SuccessCode;
import com.americanstartup.pillme.global.response.JSONResponse;
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

    //유저의 채팅방 정보 조회
    @GetMapping("/list")
    public ResponseEntity<JSONResponse<List<ChatRoomResponse>>> getUserChatRooms(@Auth Member member){
        return ResponseEntity.ok(JSONResponse.onSuccess(chatRoomService.getUserChatRoom(member)));
    }

    //채팅방 없을 시 채팅방 생성 or 채팅방 존재할 경우 이전 채팅방 정보 불러오기(redis에 유저 정보 추가)
    @PostMapping
    public ResponseEntity<JSONResponse<ChatRoomResponse>> getOrCreateChatRoom(@RequestBody ChatRoomRequest chatRoomRequest, @Auth Member member){
        return ResponseEntity.ok(JSONResponse.onSuccess(chatRoomService.getOrCreateChatRoom(chatRoomRequest, member.getId())));
    }

    //채팅방 지우기
    @DeleteMapping("/{chatRoom}")
    public ResponseEntity<JSONResponse<Void>> deleteChatRoom(@PathVariable Long chatRoom){
        chatRoomService.deleteChatRoom(chatRoom);
        return ResponseEntity.ok(JSONResponse.of(SuccessCode.CHATROOM_DELETE_SUCCESS));
    }
    //채팅방을 나갈 시 redis에서 유저 정보 삭제
    @PostMapping("leave/{chatRoomId}")
    public ResponseEntity<JSONResponse<Void>> leaveChatRoom(@PathVariable Long chatRoomId, @Auth Member member){
        chatRedisService.leaveChatRoom(chatRoomId, member.getId());
        return ResponseEntity.ok(JSONResponse.of(SuccessCode.CHATROOM_LEAVE_SUCCESS));
    }
}
