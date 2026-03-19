package com.futela.api.infrastructure.persistence.adapter.messaging;

import com.futela.api.domain.enums.NotificationStatus;
import com.futela.api.domain.enums.NotificationType;
import com.futela.api.domain.model.messaging.Notification;
import com.futela.api.domain.port.out.messaging.NotificationRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.messaging.NotificationEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.NotificationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final JpaNotificationRepository jpaRepository;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = notification.id() != null
                ? jpaRepository.findById(notification.id()).orElse(new NotificationEntity())
                : new NotificationEntity();
        entity.setType(notification.type());
        entity.setStatus(notification.status());
        entity.setTitle(notification.title());
        entity.setBody(notification.body());
        entity.setChannel(notification.channel());
        entity.setData(notification.data());
        entity.setRelatedEntityId(notification.relatedEntityId());
        entity.setRelatedEntityType(notification.relatedEntityType());
        entity.setRead(notification.isRead());
        entity.setReadAt(notification.readAt());
        return NotificationPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(NotificationPersistenceMapper::toDomain);
    }

    @Override
    public Page<Notification> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable)
                .map(NotificationPersistenceMapper::toDomain);
    }

    @Override
    public List<Notification> findUnreadByUserId(UUID userId) {
        return jpaRepository.findUnreadByUserId(userId).stream()
                .map(NotificationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        return jpaRepository.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.setRead(true);
            entity.setReadAt(Instant.now());
            entity.setStatus(NotificationStatus.READ);
            jpaRepository.save(entity);
        });
    }

    @Override
    public void markAllAsReadByUserId(UUID userId) {
        jpaRepository.markAllAsReadByUserId(userId, Instant.now(), NotificationStatus.READ);
    }

    @Override
    public void softDelete(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.softDelete();
            jpaRepository.save(entity);
        });
    }
}
