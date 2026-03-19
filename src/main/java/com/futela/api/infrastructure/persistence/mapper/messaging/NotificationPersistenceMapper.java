package com.futela.api.infrastructure.persistence.mapper.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;
import com.futela.api.domain.model.messaging.Notification;
import com.futela.api.infrastructure.persistence.entity.messaging.NotificationEntity;
import com.futela.api.infrastructure.persistence.mapper.property.PropertyPersistenceMapper;

public final class NotificationPersistenceMapper {

    private NotificationPersistenceMapper() {
    }

    public static Notification toDomain(NotificationEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getUser().getId(),
                entity.getType(),
                entity.getStatus(),
                entity.getTitle(),
                entity.getBody(),
                entity.getChannel(),
                PropertyPersistenceMapper.jsonNodeToMap(entity.getData()),
                entity.getRelatedEntityId(),
                entity.getRelatedEntityType(),
                entity.isRead(),
                entity.getReadAt(),
                entity.getCompany() != null ? entity.getCompany().getId() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public static NotificationResponse toResponse(NotificationEntity entity) {
        return new NotificationResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getType(),
                entity.getStatus(),
                entity.getTitle(),
                entity.getBody(),
                entity.getChannel(),
                PropertyPersistenceMapper.jsonNodeToMap(entity.getData()),
                entity.getRelatedEntityId(),
                entity.getRelatedEntityType(),
                entity.isRead(),
                entity.getReadAt(),
                entity.getCreatedAt()
        );
    }
}
