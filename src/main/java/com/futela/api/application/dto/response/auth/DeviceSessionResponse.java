package com.futela.api.application.dto.response.auth;

import com.futela.api.domain.model.auth.DeviceSession;

import java.time.Instant;
import java.util.UUID;

public record DeviceSessionResponse(
        UUID id,
        String deviceName,
        String deviceFingerprint,
        String ipAddress,
        String location,
        boolean isActive,
        boolean isTrusted,
        boolean isCurrent,
        Instant lastActiveAt,
        Instant createdAt
) {
    public static DeviceSessionResponse fromDomain(DeviceSession session, boolean isCurrent) {
        return new DeviceSessionResponse(
                session.id(),
                session.deviceName() != null ? session.deviceName() : "Appareil inconnu",
                session.deviceFingerprint(),
                session.ipAddress(),
                session.location(),
                session.isActive(),
                session.isTrusted(),
                isCurrent,
                session.lastActiveAt(),
                session.createdAt()
        );
    }
}
