package com.futela.api.application.usecase.messaging;

import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.DeleteMessageUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.MessageEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteMessageService implements DeleteMessageUseCase {

    private final JpaMessageRepository messageRepository;

    @Override
    public void execute(UUID messageId, UUID userId) {
        MessageEntity message = messageRepository.findById(messageId)
                .filter(m -> m.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Message", messageId.toString()));

        if (!message.getSender().getId().equals(userId)) {
            throw new InvalidOperationException("Accès refusé : vous ne pouvez supprimer que vos propres messages");
        }

        message.softDelete();
        messageRepository.save(message);

        log.info("Message supprimé : {} par utilisateur {}", messageId, userId);
    }
}
