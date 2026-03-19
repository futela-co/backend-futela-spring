package com.futela.api.infrastructure.persistence.mapper.messaging;

import com.futela.api.application.dto.response.messaging.MessageResponse;
import com.futela.api.domain.model.messaging.Message;
import com.futela.api.infrastructure.persistence.entity.messaging.MessageEntity;

public final class MessagePersistenceMapper {

    private MessagePersistenceMapper() {
    }

    public static Message toDomain(MessageEntity entity) {
        return new Message(
                entity.getId(),
                entity.getConversation().getId(),
                entity.getSender().getId(),
                entity.getSender().getFirstName() + " " + entity.getSender().getLastName(),
                entity.getType(),
                entity.getContent(),
                entity.isRead(),
                entity.getReadAt(),
                entity.getCompany() != null ? entity.getCompany().getId() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public static MessageResponse toResponse(MessageEntity entity) {
        return new MessageResponse(
                entity.getId(),
                entity.getConversation().getId(),
                entity.getSender().getId(),
                entity.getSender().getFirstName() + " " + entity.getSender().getLastName(),
                entity.getContent(),
                entity.getType(),
                entity.isRead(),
                entity.getReadAt(),
                entity.getCreatedAt()
        );
    }
}
