package com.ssafy.pillme.notification.infrastructure.repository;

import com.ssafy.pillme.notification.domain.entity.Notification;
import com.ssafy.pillme.notification.domain.vo.NotificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  NotificationRepository extends JpaRepository<Notification, Long> {
    // 삭제되지 않은 알림 조회
    List<Notification> findAllByReceiverIdAndDeletedFalse(Long id);

    List<Notification> findAllByIdInAndReceiverIdAndDeletedFalse(List<Long> ids, Long receiverId);

    // 발신자 + 수신자 + 알림 코드에 대한 알림 조회
    @Query("select n from Notification n where n.sender.id = :senderId and n.receiver.id = :receiverId and n.code = :notificationCode and n.deleted = false")
    Optional<Notification> findBySenderIdAndReceiverIdAndCodeAndDeletedFalse(@Param("senderId")Long senderId, @Param("receiverId")Long receiverId, @Param("notificationCode")NotificationCode notificationCode);
}
