package com.ssafy.pillme.notification.domain.entity;

import com.ssafy.pillme.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "fcm_token")
@Getter
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
