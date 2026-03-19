package com.futela.api.infrastructure.persistence.adapter.messaging;

import com.futela.api.domain.model.messaging.Conversation;
import com.futela.api.domain.port.out.messaging.ConversationRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.ConversationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConversationRepositoryAdapter implements ConversationRepositoryPort {

    private final JpaConversationRepository jpaRepository;

    @Override
    public Conversation save(Conversation conversation) {
        ConversationEntity entity = jpaRepository.findById(conversation.id())
                .orElse(new ConversationEntity());
        entity.setSubject(conversation.subject());
        entity.setPropertyId(conversation.propertyId());
        entity.setLastMessageAt(conversation.lastMessageAt());
        entity.setArchived(conversation.isArchived());
        return ConversationPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Conversation> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(ConversationPersistenceMapper::toDomain);
    }

    @Override
    public List<Conversation> findByUserId(UUID userId) {
        return jpaRepository.findByParticipantId(userId).stream()
                .map(ConversationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Conversation> findByParticipants(UUID participant1Id, UUID participant2Id) {
        return jpaRepository.findByParticipants(participant1Id, participant2Id)
                .map(ConversationPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Conversation> findByParticipantsAndProperty(UUID participant1Id, UUID participant2Id, UUID propertyId) {
        return jpaRepository.findByParticipantsAndProperty(participant1Id, participant2Id, propertyId)
                .map(ConversationPersistenceMapper::toDomain);
    }

    @Override
    public List<Conversation> searchByUserId(UUID userId, String query, UUID propertyId, boolean includeArchived) {
        return jpaRepository.searchByUserId(userId, query, propertyId, includeArchived).stream()
                .map(ConversationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void softDelete(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.softDelete();
            jpaRepository.save(entity);
        });
    }
}
