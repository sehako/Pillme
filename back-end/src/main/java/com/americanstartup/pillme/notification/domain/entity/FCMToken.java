package com.americanstartup.pillme.notification.domain.entity;

import com.americanstartup.pillme.auth.domain.entity.Member;
import com.americanstartup.pillme.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "fcm_token")
@Getter
public class FCMToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String token;

    public static FCMToken create(Member member, String token) {
        return FCMToken.builder()
                .member(member)
                .token(token)
                .build();
    }
}
