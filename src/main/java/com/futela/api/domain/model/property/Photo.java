package com.futela.api.domain.model.property;

import java.time.Instant;
import java.util.UUID;

public record Photo(
        UUID id,
        UUID propertyId,
        String url,
        String caption,
        int position,
        boolean isPrimary,
        Instant createdAt
) {}
