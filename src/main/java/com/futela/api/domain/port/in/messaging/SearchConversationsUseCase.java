package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;

import java.util.List;
import java.util.UUID;

public interface SearchConversationsUseCase {

    List<ConversationResponse> execute(UUID userId, String query, UUID propertyId, boolean includeArchived);
}
