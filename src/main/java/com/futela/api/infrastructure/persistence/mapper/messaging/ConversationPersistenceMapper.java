package com.futela.api.infrastructure.persistence.mapper.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.domain.model.messaging.Conversation;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;

import java.util.List;

public final class ConversationPersistenceMapper {

    private ConversationPersistenceMapper() {
    }

    public static Conversation toDomain(ConversationEntity entity) {
        return new Conversation(
                entity.getId(),
                entity.getSubject(),
                entity.getParticipants().stream().map(UserEntity::getId).toList(),
                entity.getPropertyId(),
                entity.getLastMessageAt(),
                entity.isArchived(),
                entity.getCompany() != null ? entity.getCompany().getId() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public static ConversationResponse toResponse(ConversationEntity entity, long unreadCount) {
        List<ConversationResponse.ParticipantInfo> participants = entity.getParticipants().stream()
                .map(u -> new ConversationResponse.ParticipantInfo(
                        u.getId(),
                        u.getFirstName() + " " + u.getLastName()
                ))
                .toList();

        return new ConversationResponse(
                entity.getId(),
                entity.getSubject(),
                participants,
                entity.getPropertyId(),
                entity.getLastMessageAt(),
                entity.isArchived(),
                unreadCount,
                entity.getCreatedAt()
        );
    }
}
