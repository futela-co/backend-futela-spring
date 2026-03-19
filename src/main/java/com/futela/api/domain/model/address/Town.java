package com.futela.api.domain.model.address;

import java.time.Instant;
import java.util.UUID;

public record Town(
        UUID id,
        String name,
        String zipCode,
        boolean isActive,
        UUID cityId,
        String cityName,
        Instant createdAt,
        Instant updatedAt
) {}
