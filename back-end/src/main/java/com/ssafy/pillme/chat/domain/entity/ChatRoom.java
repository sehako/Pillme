package com.ssafy.pillme.chat.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="chat_room", uniqueConstraints = @UniqueConstraint(columnNames = {"care_user_id","user_id"}))
@NoArgsConstructor
@Getter
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="care_user_id", nullable = false)
    private Long careUserId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    public void updateChatRoom(Long care_user_id, Long user_id){
        this.careUserId = care_user_id;
        this.userId = user_id;
    }
}
