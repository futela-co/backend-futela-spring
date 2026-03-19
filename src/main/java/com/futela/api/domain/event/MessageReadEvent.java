package com.futela.api.domain.event;

import java.util.UUID;

public record MessageReadEvent(
        UUID messageId,
        UUID conversationId,
        UUID readByUserId
) {
}
