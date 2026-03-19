package com.futela.api.domain.model.address;

import java.time.Instant;
import java.util.UUID;

public record Country(
        UUID id,
        String name,
        String code,
        String phoneCode,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
