package com.ssafy.pillme.chat.infrastructure.repository;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByCareUserOrUser(Member careUser, Member user);

    @Query("SELECT c FROM ChatRoom c WHERE (c.careUser = :user1 AND c.user = :user2) OR (c.careUser = :user2 AND c.user = :user1)")
    Optional<ChatRoom> findByUsers(@Param("user1") Member user1, @Param("user2") Member user2);

    void deleteByCareUserAndUser(Member careUser, Member user);
}