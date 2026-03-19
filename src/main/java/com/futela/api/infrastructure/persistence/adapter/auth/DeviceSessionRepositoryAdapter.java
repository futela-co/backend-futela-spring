package com.futela.api.infrastructure.persistence.adapter.auth;

import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.auth.DeviceSessionEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.mapper.auth.DeviceSessionPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.auth.JpaDeviceSessionRepository;
import com.futela.api.infrastructure.persistence.repository.auth.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeviceSessionRepositoryAdapter implements DeviceSessionRepositoryPort {

    private final JpaDeviceSessionRepository jpaRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<DeviceSession> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(DeviceSessionPersistenceMapper::toDomain);
    }

    @Override
    public Optional<DeviceSession> findActiveByUserAndFingerprint(UUID userId, String deviceFingerprint) {
        return jpaRepository.findActiveByUserAndFingerprint(userId, deviceFingerprint)
                .map(DeviceSessionPersistenceMapper::toDomain);
    }

    @Override
    public List<DeviceSession> findActiveSessionsByUser(UUID userId) {
        return jpaRepository.findActiveSessionsByUser(userId).stream()
                .map(DeviceSessionPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public DeviceSession save(DeviceSession session) {
        UserEntity userEntity = jpaUserRepository.getReferenceById(session.userId());
        DeviceSessionEntity entity = DeviceSessionPersistenceMapper.toEntity(session, userEntity);
        DeviceSessionEntity saved = jpaRepository.save(entity);
        return DeviceSessionPersistenceMapper.toDomain(saved);
    }

    @Override
    public void revokeSession(UUID sessionId) {
        jpaRepository.revokeSession(sessionId, Instant.now());
    }

    @Override
    public int revokeAllSessionsForUser(UUID userId) {
        return jpaRepository.revokeAllSessionsForUser(userId, Instant.now());
    }
}
