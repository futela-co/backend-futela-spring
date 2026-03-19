package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;

import java.util.UUID;

public interface GetConversationByIdUseCase {

    ConversationResponse execute(UUID conversationId, UUID userId);
}
