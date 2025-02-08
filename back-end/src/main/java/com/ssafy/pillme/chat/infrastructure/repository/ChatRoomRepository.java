package com.ssafy.pillme.chat.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByCareUserOrUser(Member careUser, Member user);

    Optional<ChatRoom> findByCareUserAndUser(Member careUser, Member user);

    void deleteByCareUserAndUser(Member careUser, Member user);
}