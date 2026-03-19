package com.futela.api.application.dto.request.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RespondToContactRequest(
        @NotBlank(message = "La réponse est requise")
        @Size(min = 10, message = "La réponse doit contenir au moins 10 caractères")
        String response,

        @NotBlank(message = "Le statut est requis")
        String status
) {
}
