package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.ArchiveConversationUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.ConversationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ArchiveConversationService implements ArchiveConversationUseCase {

    private final JpaConversationRepository conversationRepository;

    @Override
    public ConversationResponse execute(UUID conversationId, UUID userId, boolean archive) {
        ConversationEntity conversation = conversationRepository.findById(conversationId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId.toString()));

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));
        if (!isParticipant) {
            throw new InvalidOperationException("Accès refusé : vous ne participez pas à cette conversation");
        }

        conversation.setArchived(archive);
        conversationRepository.save(conversation);

        log.info("Conversation {} : {} par utilisateur {}", archive ? "archivée" : "désarchivée", conversationId, userId);

        return ConversationPersistenceMapper.toResponse(conversation, 0);
    }
}
