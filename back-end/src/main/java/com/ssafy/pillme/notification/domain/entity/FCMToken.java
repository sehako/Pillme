package com.ssafy.pillme.notification.domain.entity;

import com.ssafy.pillme.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "fcm_token")
public class FCMToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String token;

    public static FCMToken create(Integer userId, String token) {
        return FCMToken.builder()
                .userId(userId)
                .token(token)
                .build();
    }
}
