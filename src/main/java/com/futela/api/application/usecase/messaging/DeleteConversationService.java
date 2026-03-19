package com.futela.api.application.usecase.messaging;

import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.DeleteConversationUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
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
public class DeleteConversationService implements DeleteConversationUseCase {

    private final JpaConversationRepository conversationRepository;

    @Override
    public void execute(UUID conversationId, UUID userId) {
        ConversationEntity conversation = conversationRepository.findById(conversationId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId.toString()));

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));
        if (!isParticipant) {
            throw new InvalidOperationException("Accès refusé : vous ne participez pas à cette conversation");
        }

        conversation.softDelete();
        conversationRepository.save(conversation);

        log.info("Conversation supprimée : {} par utilisateur {}", conversationId, userId);
    }
}
