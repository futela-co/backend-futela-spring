package com.futela.api.application.dto.response.review;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID propertyId,
        String propertyTitle,
        UUID userId,
        String userFullName,
        int rating,
        String comment,
        boolean isApproved,
        boolean isFlagged,
        String flagReason,
        Instant createdAt,
        Instant updatedAt
) {}
