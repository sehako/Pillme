package com.ssafy.pillme.notification.infrastructure.repository;

import com.ssafy.pillme.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 삭제되지 않은 알림 조회
    List<Notification> findAllByReceiverIdAndDeletedFalse(Long id);

    List<Notification> findAllByIdInAndReceiverId(List<Long> ids, Long receiverId);
}
