package com.ssafy.pillme.notification.domain.entity;

import com.ssafy.pillme.auth.domain.entity.Member;
import com.ssafy.pillme.global.entity.BaseEntity;
import com.ssafy.pillme.notification.domain.vo.NotificationCode;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자를 protected로 제한 -> 생성 메소드로 객체 생성하도록 유도(무분별한 객체 생성 방지)
@Builder
@AllArgsConstructor
@Entity
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 보낸 사람(발송자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    // 받는 사람(수신자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    // 알림 종류에 대한 코드
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationCode code;

    // 알림 내용
    private String content;

    // 알림을 읽었는지 여부
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean confirm;

    public static Notification createDependencyRequest(Member sender, Member receiver) {
        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .code(NotificationCode.DEPENDENCY_REQUEST)
                .content(sender.getName() + "님이 " + NotificationCode.DEPENDENCY_REQUEST.getMessage())
                .build();
    }

    public static Notification createDependencyAccept(Member sender, Member receiver) {
        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .code(NotificationCode.DEPENDENCY_ACCEPT)
                .content(sender.getName() + "님과 " + NotificationCode.DEPENDENCY_ACCEPT.getMessage())
                .build();
    }

    public static Notification createDependencyReject(Member sender, Member receiver) {
        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .code(NotificationCode.DEPENDENCY_REJECT)
                .content(sender.getName() + "님과 " + NotificationCode.DEPENDENCY_REJECT.getMessage())
                .build();
    }

    public static Notification createMedicineRequest(Member sender, Member receiver) {
        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .code(NotificationCode.MEDICINE_REQUEST)
                .content(sender.getName() + "님이 " + NotificationCode.MEDICINE_REQUEST.getMessage())
                .build();
    }

    public static Notification createMedicineAccept(Member sender, Member receiver) {
        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .code(NotificationCode.MEDICINE_ACCEPT)
                .content(sender.getName() + "님의 " + NotificationCode.MEDICINE_ACCEPT.getMessage())
                .build();
    }

    public static Notification createMedicineReject(Member sender, Member receiver) {
        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .code(NotificationCode.MEDICINE_REJECT)
                .content(sender.getName() + "님의 " + NotificationCode.MEDICINE_REJECT.getMessage())
                .build();
    }

    public static Notification createDependencyDeleteRequest(Member sender, Member receiver) {
        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .code(NotificationCode.DEPENDENCY_DELETE_REQUEST)
                .content(sender.getName() + "님이 " + NotificationCode.DEPENDENCY_DELETE_REQUEST.getMessage())
                .build();
    }

    public static Notification createDependencyDeleteAccept(Member sender, Member receiver) {
        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .code(NotificationCode.DEPENDENCY_DELETE_ACCEPT)
                .content(sender.getName() + "님과 " + NotificationCode.DEPENDENCY_DELETE_ACCEPT.getMessage())
                .build();
    }

    public void updateConfirmStatus(boolean confirm) {
        this.confirm = confirm;
    }

    public void updateDeleteStatus() {
        delete();
    }
}
