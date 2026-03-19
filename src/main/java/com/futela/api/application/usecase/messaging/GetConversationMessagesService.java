package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.MessageResponse;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.GetConversationMessagesUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.MessagePersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetConversationMessagesService implements GetConversationMessagesUseCase {

    private final JpaConversationRepository conversationRepository;
    private final JpaMessageRepository messageRepository;

    @Override
    public Page<MessageResponse> execute(UUID conversationId, UUID userId, Pageable pageable) {
        ConversationEntity conversation = conversationRepository.findById(conversationId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId.toString()));

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));
        if (!isParticipant) {
            throw new InvalidOperationException("L'utilisateur ne participe pas à cette conversation");
        }

        return messageRepository.findByConversationId(conversationId, pageable)
                .map(MessagePersistenceMapper::toResponse);
    }
}
