package com.futela.api.domain.model.address;

import java.time.Instant;
import java.util.UUID;

public record Province(
        UUID id,
        String name,
        String code,
        boolean isActive,
        UUID countryId,
        String countryName,
        Instant createdAt,
        Instant updatedAt
) {}
