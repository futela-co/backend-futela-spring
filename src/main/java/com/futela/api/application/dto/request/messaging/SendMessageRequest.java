package com.futela.api.application.dto.request.messaging;

import com.futela.api.domain.enums.MessageType;
import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank(message = "Le contenu du message est requis")
        String content,

        MessageType type
) {
    public SendMessageRequest {
        if (type == null) {
            type = MessageType.TEXT;
        }
    }
}
