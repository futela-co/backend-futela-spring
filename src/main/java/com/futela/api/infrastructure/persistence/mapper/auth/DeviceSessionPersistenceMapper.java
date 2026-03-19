package com.futela.api.infrastructure.persistence.mapper.auth;

import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.infrastructure.persistence.entity.auth.DeviceSessionEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;

public final class DeviceSessionPersistenceMapper {

    private DeviceSessionPersistenceMapper() {}

    public static DeviceSession toDomain(DeviceSessionEntity entity) {
        if (entity == null) return null;
        return new DeviceSession(
                entity.getId(),
                entity.getUser().getId(),
                entity.getDeviceName(),
                entity.getDeviceFingerprint(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getLocation(),
                entity.isActive(),
                entity.isTrusted(),
                entity.getLastActiveAt(),
                entity.getRevokedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static DeviceSessionEntity toEntity(DeviceSession domain, UserEntity userEntity) {
        if (domain == null) return null;
        DeviceSessionEntity entity = new DeviceSessionEntity();
        entity.setId(domain.id());
        entity.setUser(userEntity);
        entity.setDeviceName(domain.deviceName());
        entity.setDeviceFingerprint(domain.deviceFingerprint());
        entity.setIpAddress(domain.ipAddress());
        entity.setUserAgent(domain.userAgent());
        entity.setLocation(domain.location());
        entity.setActive(domain.isActive());
        entity.setTrusted(domain.isTrusted());
        entity.setLastActiveAt(domain.lastActiveAt());
        entity.setRevokedAt(domain.revokedAt());
        return entity;
    }
}
