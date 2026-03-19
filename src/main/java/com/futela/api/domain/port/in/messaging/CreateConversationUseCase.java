package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.request.messaging.CreateConversationRequest;
import com.futela.api.application.dto.response.messaging.ConversationResponse;

import java.util.UUID;

public interface CreateConversationUseCase {

    ConversationResponse execute(CreateConversationRequest request, UUID currentUserId);
}
