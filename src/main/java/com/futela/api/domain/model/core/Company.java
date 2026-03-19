package com.futela.api.domain.model.core;

import java.time.Instant;
import java.util.UUID;

public record Company(
        UUID id,
        String name,
        String slug,
        String email,
        String phone,
        String logo,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
    public static Company create(String name, String slug) {
        return new Company(null, name, slug, null, null, null, true, Instant.now(), Instant.now());
    }
}
