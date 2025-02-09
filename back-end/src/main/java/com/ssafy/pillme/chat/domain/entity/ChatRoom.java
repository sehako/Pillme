package com.ssafy.pillme.chat.domain.entity;

import com.ssafy.pillme.auth.domain.entity.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="care_user_id", nullable = false)
    private Member sendUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private Member receiveUser;

    public void updateChatRoom(Member sendUser, Member receiveuser){
        this.sendUser = sendUser;
        this.receiveUser = receiveuser;
    }
}