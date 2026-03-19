package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.MessageResponse;

import java.util.UUID;

public interface MarkMessageAsReadUseCase {

    MessageResponse execute(UUID messageId, UUID userId);
}
