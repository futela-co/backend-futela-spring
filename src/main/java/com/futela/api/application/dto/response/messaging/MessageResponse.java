package com.futela.api.application.dto.response.messaging;

import com.futela.api.domain.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID conversationId,
        UUID senderId,
        String senderName,
        String content,
        MessageType type,
        boolean isRead,
        Instant readAt,
        Instant createdAt
) {
}
