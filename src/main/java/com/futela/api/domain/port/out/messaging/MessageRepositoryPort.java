package com.futela.api.domain.port.out.messaging;

import com.futela.api.domain.model.messaging.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepositoryPort {

    Message save(Message message);

    Optional<Message> findById(UUID id);

    Page<Message> findByConversationId(UUID conversationId, Pageable pageable);

    long countUnreadByUserId(UUID userId);

    void softDelete(UUID id);
}
