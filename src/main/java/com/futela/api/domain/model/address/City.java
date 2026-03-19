package com.futela.api.domain.model.address;

import java.time.Instant;
import java.util.UUID;

public record City(
        UUID id,
        String name,
        String zipCode,
        boolean isActive,
        UUID provinceId,
        String provinceName,
        Instant createdAt,
        Instant updatedAt
) {}
