package com.futela.api.domain.model.messaging;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Conversation(
        UUID id,
        String subject,
        List<UUID> participantIds,
        UUID propertyId,
        Instant lastMessageAt,
        boolean isArchived,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
}
