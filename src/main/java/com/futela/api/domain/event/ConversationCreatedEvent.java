package com.futela.api.domain.event;

import java.util.List;
import java.util.UUID;

public record ConversationCreatedEvent(
        UUID conversationId,
        String subject,
        List<UUID> participantIds,
        UUID propertyId,
        UUID companyId
) {
}
