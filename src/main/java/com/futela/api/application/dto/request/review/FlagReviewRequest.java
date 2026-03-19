package com.futela.api.application.dto.request.review;

import jakarta.validation.constraints.NotBlank;

public record FlagReviewRequest(
        @NotBlank(message = "La raison du signalement est requise")
        String reason
) {}
