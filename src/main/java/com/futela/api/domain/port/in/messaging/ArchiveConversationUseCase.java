package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;

import java.util.UUID;

public interface ArchiveConversationUseCase {

    ConversationResponse execute(UUID conversationId, UUID userId, boolean archive);
}
