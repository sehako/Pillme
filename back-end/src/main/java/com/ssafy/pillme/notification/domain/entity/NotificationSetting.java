package com.ssafy.pillme.notification.domain.entity;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.notification.presentation.request.NotificationSettingRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.Optional;

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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private LocalTime morning; // localTime은 HH:mm:ss 형식 DB에는 TIME으로 저장
    private LocalTime lunch; // ZonedDateTime은 글로벌 서비스 전략으로 시간대 정보를 포함하는 타임스탬프를 저장
    private LocalTime dinner; // 확장성을 고려한다면 ZonedDateTime을 사용하는 것이 좋음
    private LocalTime sleep;

    /*
    * 알림 시간이 변경될 경우 엔티티의 값을 변경하는 메소드
    * 알림 시간이 변경되지 않은 경우 null이 들어올 수 있으므로 Optional로 감싸서 값이 있는 경우에만 변경
    * */
    public void update(NotificationSettingRequest notificationSettingRequest) {
        Optional.ofNullable(notificationSettingRequest.morning())
                .ifPresent(morning -> this.morning = LocalTime.parse(morning));

        Optional.ofNullable(notificationSettingRequest.lunch())
                .ifPresent(lunch -> this.lunch = LocalTime.parse(lunch));

        Optional.ofNullable(notificationSettingRequest.dinner())
                .ifPresent(dinner -> this.dinner = LocalTime.parse(dinner));

        Optional.ofNullable(notificationSettingRequest.sleep())
                .ifPresent(sleep -> this.sleep = LocalTime.parse(sleep));
    }
}
