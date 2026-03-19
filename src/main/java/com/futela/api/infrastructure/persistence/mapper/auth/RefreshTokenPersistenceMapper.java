package com.futela.api.infrastructure.persistence.mapper.auth;

import com.futela.api.domain.model.auth.RefreshToken;
import com.futela.api.infrastructure.persistence.entity.auth.DeviceSessionEntity;
import com.futela.api.infrastructure.persistence.entity.auth.RefreshTokenEntity;

public final class RefreshTokenPersistenceMapper {

    private RefreshTokenPersistenceMapper() {}

    public static RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) return null;
        return new RefreshToken(
                entity.getId(),
                entity.getDeviceSession().getId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getUsedAt(),
                entity.isRevoked(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static RefreshTokenEntity toEntity(RefreshToken domain, DeviceSessionEntity sessionEntity) {
        if (domain == null) return null;
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(domain.id());
        entity.setDeviceSession(sessionEntity);
        entity.setTokenHash(domain.tokenHash());
        entity.setExpiresAt(domain.expiresAt());
        entity.setUsedAt(domain.usedAt());
        entity.setRevoked(domain.isRevoked());
        return entity;
    }
}
