package com.futela.api.application.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReviewRequest(
        @NotNull(message = "L'identifiant de la propriété est requis")
        UUID propertyId,

        @NotNull(message = "La note est requise")
        @Min(value = 1, message = "La note doit être entre 1 et 5")
        @Max(value = 5, message = "La note doit être entre 1 et 5")
        Integer rating,

        String comment
) {}
