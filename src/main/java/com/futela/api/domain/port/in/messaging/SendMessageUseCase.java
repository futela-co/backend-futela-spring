package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.request.messaging.SendMessageRequest;
import com.futela.api.application.dto.response.messaging.MessageResponse;

import java.util.UUID;

public interface SendMessageUseCase {

    MessageResponse execute(SendMessageRequest request, UUID senderId);
}
