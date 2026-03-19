package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.GetConversationByIdUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.ConversationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetConversationByIdService implements GetConversationByIdUseCase {

    private final JpaConversationRepository conversationRepository;

    @Override
    public ConversationResponse execute(UUID conversationId, UUID userId) {
        ConversationEntity entity = conversationRepository.findById(conversationId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId.toString()));

        boolean isParticipant = entity.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));
        if (!isParticipant) {
            throw new InvalidOperationException("Accès refusé : vous ne participez pas à cette conversation");
        }

        return ConversationPersistenceMapper.toResponse(entity, 0);
    }
}
