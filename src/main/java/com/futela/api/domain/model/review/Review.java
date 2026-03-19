package com.futela.api.domain.model.review;

import java.time.Instant;
import java.util.UUID;

public record Review(
        UUID id,
        UUID propertyId,
        UUID userId,
        UUID companyId,
        int rating,
        String comment,
        boolean isApproved,
        boolean isFlagged,
        String flagReason,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {}
