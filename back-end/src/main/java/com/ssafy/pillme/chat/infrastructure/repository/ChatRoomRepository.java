package com.ssafy.pillme.chat.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findBySendUserOrReceiveUser(Member sendUser, Member receiveUser);

    @Query("SELECT c FROM ChatRoom c WHERE (c.sendUser = :user1 AND c.receiveUser = :user2) OR (c.sendUser = :user2 AND c.receiveUser = :user1)")
    Optional<ChatRoom> findByUsers(@Param("user1") Member sendUser, @Param("user2") Member receiveUser);

    void deleteBySendUserAndReceiveUser(Member sendUser, Member receiveUser);
}