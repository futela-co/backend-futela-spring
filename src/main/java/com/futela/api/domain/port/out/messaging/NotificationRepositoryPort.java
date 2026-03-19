package com.futela.api.domain.port.out.messaging;

import com.futela.api.domain.enums.NotificationType;
import com.futela.api.domain.model.messaging.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepositoryPort {

    Notification save(Notification notification);

    Optional<Notification> findById(UUID id);

    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    List<Notification> findUnreadByUserId(UUID userId);

    long countUnreadByUserId(UUID userId);

    void markAsRead(UUID id);

    void markAllAsReadByUserId(UUID userId);

    void softDelete(UUID id);
}
