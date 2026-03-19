package com.futela.api.application.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateReviewRequest(
        @Min(value = 1, message = "La note doit être entre 1 et 5")
        @Max(value = 5, message = "La note doit être entre 1 et 5")
        Integer rating,

        String comment
) {}
