package com.futela.api.domain.model.property;

import java.time.Instant;
import java.util.UUID;

public record Listing(
        UUID id,
        UUID userId,
        UUID propertyId,
        UUID companyId,
        Instant createdAt
) {}
