package com.futela.api.application.dto.response.messaging;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversationResponse(
        UUID id,
        String subject,
        List<ParticipantInfo> participants,
        UUID propertyId,
        Instant lastMessageAt,
        boolean isArchived,
        long unreadCount,
        Instant createdAt
) {
    public record ParticipantInfo(
            UUID id,
            String name
    ) {
    }
}
