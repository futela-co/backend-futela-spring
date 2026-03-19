package com.futela.api.domain.port.in.messaging;

import java.util.UUID;

public interface DeleteConversationUseCase {

    void execute(UUID conversationId, UUID userId);
}
