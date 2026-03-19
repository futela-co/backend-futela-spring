package com.futela.api.domain.port.out.auth;

import com.futela.api.domain.model.auth.DeviceSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceSessionRepositoryPort {

    Optional<DeviceSession> findById(UUID id);

    Optional<DeviceSession> findActiveByUserAndFingerprint(UUID userId, String deviceFingerprint);

    List<DeviceSession> findActiveSessionsByUser(UUID userId);

    DeviceSession save(DeviceSession session);

    void revokeSession(UUID sessionId);

    int revokeAllSessionsForUser(UUID userId);
}
