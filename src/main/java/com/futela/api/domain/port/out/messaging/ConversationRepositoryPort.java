package com.futela.api.domain.port.out.messaging;

import com.futela.api.domain.model.messaging.Conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepositoryPort {

    Conversation save(Conversation conversation);

    Optional<Conversation> findById(UUID id);

    List<Conversation> findByUserId(UUID userId);

    Optional<Conversation> findByParticipants(UUID participant1Id, UUID participant2Id);

    Optional<Conversation> findByParticipantsAndProperty(UUID participant1Id, UUID participant2Id, UUID propertyId);

    List<Conversation> searchByUserId(UUID userId, String query, UUID propertyId, boolean includeArchived);

    void softDelete(UUID id);
}
