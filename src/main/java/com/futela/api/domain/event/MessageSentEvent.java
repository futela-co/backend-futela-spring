package com.futela.api.domain.event;

import java.util.UUID;

public record MessageSentEvent(
        UUID messageId,
        UUID conversationId,
        UUID senderId,
        String senderName,
        String contentPreview,
        UUID companyId
) {
}
