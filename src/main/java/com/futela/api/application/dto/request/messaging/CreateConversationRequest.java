package com.futela.api.application.dto.request.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateConversationRequest(
        @NotNull(message = "L'identifiant du participant est requis")
        UUID participant2Id,

        @NotBlank(message = "Le sujet est requis")
        String subject,

        UUID propertyId
) {
}
