package com.futela.api.domain.model.property;

import java.time.Instant;
import java.util.UUID;

public record Category(
        UUID id,
        String name,
        String slug,
        String description,
        String icon,
        boolean isActive,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt
) {}
