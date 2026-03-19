package com.futela.api.domain.model.address;

import java.time.Instant;
import java.util.UUID;

public record District(
        UUID id,
        String name,
        boolean isActive,
        UUID cityId,
        String cityName,
        UUID townId,
        String townName,
        Instant createdAt,
        Instant updatedAt
) {}
