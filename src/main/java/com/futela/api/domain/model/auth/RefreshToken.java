package com.futela.api.domain.model.auth;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(
        UUID id,
        UUID deviceSessionId,
        String tokenHash,
        Instant expiresAt,
        Instant usedAt,
        boolean isRevoked,
        Instant createdAt,
        Instant updatedAt
) {
    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isValid() {
        return !isExpired() && !isUsed() && !isRevoked;
    }
}
