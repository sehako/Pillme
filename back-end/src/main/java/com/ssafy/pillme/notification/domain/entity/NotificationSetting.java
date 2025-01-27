package com.ssafy.pillme.notification.domain.entity;

import com.ssafy.pillme.notification.presentation.request.NotificationSettingRequest;
import com.ssafy.pillme.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자를 protected로 제한 -> 생성 메소드로 객체 생성하도록 유도(무분별한 객체 생성 방지)
@Builder
@AllArgsConstructor
@Entity
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스의 AUTO_INCREMENT 기능 사용
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY) // 지연 로딩을 통해 회원 데이터는 필요할 때만 가져옴
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalTime morning; // localTime은 HH:mm:ss 형식 DB에는 TIME으로 저장
    private LocalTime lunch; // ZonedDateTime은 글로벌 서비스 전략으로 시간대 정보를 포함하는 타임스탬프를 저장
    private LocalTime dinner; // 확장성을 고려한다면 ZonedDateTime을 사용하는 것이 좋음
    private LocalTime sleep;

    public void update(NotificationSettingRequest notificationSettingRequest) {
        this.morning = notificationSettingRequest.morning() == null ? null : LocalTime.parse(notificationSettingRequest.morning());
        this.lunch = notificationSettingRequest.lunch() == null ? null : LocalTime.parse(notificationSettingRequest.lunch());
        this.dinner = notificationSettingRequest.dinner() == null ? null : LocalTime.parse(notificationSettingRequest.dinner());
        this.sleep = notificationSettingRequest.sleep() == null ? null : LocalTime.parse(notificationSettingRequest.sleep());
    }
}
