package com.ssafy.pillme.chat.infrastructure.repository;

import com.ssafy.pillme.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByCareUserIdOrUserId(Long userId1, Long userId2);

    Optional<ChatRoom> findByCareUserIdAndUserId(Long care_user_id, Long user_id);
}
