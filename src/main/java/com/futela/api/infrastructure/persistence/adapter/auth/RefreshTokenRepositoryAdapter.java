package com.futela.api.infrastructure.persistence.adapter.auth;

import com.futela.api.domain.model.auth.RefreshToken;
import com.futela.api.domain.port.out.auth.RefreshTokenRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.auth.DeviceSessionEntity;
import com.futela.api.infrastructure.persistence.entity.auth.RefreshTokenEntity;
import com.futela.api.infrastructure.persistence.mapper.auth.RefreshTokenPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.auth.JpaDeviceSessionRepository;
import com.futela.api.infrastructure.persistence.repository.auth.JpaRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final JpaRefreshTokenRepository jpaRepository;
    private final JpaDeviceSessionRepository jpaDeviceSessionRepository;

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash)
                .map(RefreshTokenPersistenceMapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        DeviceSessionEntity sessionEntity = jpaDeviceSessionRepository.getReferenceById(refreshToken.deviceSessionId());
        RefreshTokenEntity entity = RefreshTokenPersistenceMapper.toEntity(refreshToken, sessionEntity);
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return RefreshTokenPersistenceMapper.toDomain(saved);
    }

    @Override
    public void markAsUsed(UUID id) {
        jpaRepository.markAsUsed(id, Instant.now());
    }

    @Override
    public void revokeAllTokensForSession(UUID sessionId) {
        jpaRepository.revokeAllTokensForSession(sessionId, Instant.now());
    }
}
