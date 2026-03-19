package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.request.messaging.CreateConversationRequest;
import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.domain.event.ConversationCreatedEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.CreateConversationUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.ConversationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateConversationService implements CreateConversationUseCase {

    private final JpaConversationRepository conversationRepository;
    private final JpaMessageRepository messageRepository;
    private final EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ConversationResponse execute(CreateConversationRequest request, UUID currentUserId) {
        UUID participant2Id = request.participant2Id();
        UUID propertyId = request.propertyId();

        // Check for existing conversation between the 2 participants
        Optional<ConversationEntity> existing = propertyId != null
                ? conversationRepository.findByParticipantsAndProperty(currentUserId, participant2Id, propertyId)
                : conversationRepository.findByParticipants(currentUserId, participant2Id);

        if (existing.isPresent()) {
            log.info("Conversation existante trouvée entre {} et {}", currentUserId, participant2Id);
            return ConversationPersistenceMapper.toResponse(existing.get(), 0);
        }

        // Load participant references
        UserEntity participant1 = entityManager.getReference(UserEntity.class, currentUserId);
        UserEntity participant2 = entityManager.find(UserEntity.class, participant2Id);
        if (participant2 == null) {
            throw new ResourceNotFoundException("User", participant2Id.toString());
        }

        // Create new conversation
        ConversationEntity entity = new ConversationEntity();
        entity.setSubject(request.subject());
        if (propertyId != null) {
            entity.setProperty(entityManager.getReference(com.futela.api.infrastructure.persistence.entity.property.PropertyEntity.class, propertyId));
        }
        entity.setArchived(false);
        entity.setCompany(participant1.getCompany());
        entity.getParticipants().add(participant1);
        entity.getParticipants().add(participant2);

        ConversationEntity saved = conversationRepository.save(entity);

        log.info("Conversation créée : {} entre {} et {}", saved.getId(), currentUserId, participant2Id);

        eventPublisher.publishEvent(new ConversationCreatedEvent(
                saved.getId(),
                saved.getSubject(),
                List.of(currentUserId, participant2Id),
                propertyId,
                participant1.getCompany().getId()
        ));

        return ConversationPersistenceMapper.toResponse(saved, 0);
    }
}
