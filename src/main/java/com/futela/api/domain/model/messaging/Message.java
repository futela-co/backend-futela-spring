package com.futela.api.domain.model.messaging;

import com.futela.api.domain.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

public record Message(
        UUID id,
        UUID conversationId,
        UUID senderId,
        String senderName,
        MessageType type,
        String content,
        boolean isRead,
        Instant readAt,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
}
