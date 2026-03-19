package com.futela.api.domain.model.auth;

import java.time.Instant;
import java.util.UUID;

public record DeviceSession(
        UUID id,
        UUID userId,
        String deviceName,
        String deviceFingerprint,
        String ipAddress,
        String userAgent,
        String location,
        boolean isActive,
        boolean isTrusted,
        Instant lastActiveAt,
        Instant revokedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public boolean isRevoked() {
        return revokedAt != null;
    }
}
