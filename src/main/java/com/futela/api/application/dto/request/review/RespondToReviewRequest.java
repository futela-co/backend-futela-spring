package com.futela.api.application.dto.request.review;

import jakarta.validation.constraints.NotBlank;

public record RespondToReviewRequest(
        @NotBlank(message = "La réponse est requise")
        String response
) {}
