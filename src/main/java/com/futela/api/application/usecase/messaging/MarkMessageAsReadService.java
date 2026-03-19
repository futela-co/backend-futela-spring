package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.MessageResponse;
import com.futela.api.domain.event.MessageReadEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.MarkMessageAsReadUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.MessageEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.MessagePersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkMessageAsReadService implements MarkMessageAsReadUseCase {

    private final JpaMessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public MessageResponse execute(UUID messageId, UUID userId) {
        MessageEntity message = messageRepository.findById(messageId)
                .filter(m -> m.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Message", messageId.toString()));

        // Cannot mark own message as read
        if (message.getSender().getId().equals(userId)) {
            throw new InvalidOperationException("Impossible de marquer son propre message comme lu");
        }

        // Verify user is participant
        boolean isParticipant = message.getConversation().getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));
        if (!isParticipant) {
            throw new InvalidOperationException("L'utilisateur ne participe pas à cette conversation");
        }

        if (!message.isRead()) {
            message.setRead(true);
            message.setReadAt(Instant.now());
            messageRepository.save(message);

            eventPublisher.publishEvent(new MessageReadEvent(
                    messageId,
                    message.getConversation().getId(),
                    userId
            ));
        }

        return MessagePersistenceMapper.toResponse(message);
    }
}
