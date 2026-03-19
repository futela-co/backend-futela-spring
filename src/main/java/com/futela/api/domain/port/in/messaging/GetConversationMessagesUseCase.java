package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetConversationMessagesUseCase {

    Page<MessageResponse> execute(UUID conversationId, UUID userId, Pageable pageable);
}
