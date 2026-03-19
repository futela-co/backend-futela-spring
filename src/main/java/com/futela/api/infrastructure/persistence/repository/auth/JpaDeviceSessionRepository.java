package com.futela.api.infrastructure.persistence.repository.auth;

import com.futela.api.infrastructure.persistence.entity.auth.DeviceSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaDeviceSessionRepository extends JpaRepository<DeviceSessionEntity, UUID> {

    @Query("SELECT ds FROM DeviceSessionEntity ds WHERE ds.user.id = :userId AND ds.deviceFingerprint = :fingerprint AND ds.isActive = true AND ds.deletedAt IS NULL")
    Optional<DeviceSessionEntity> findActiveByUserAndFingerprint(@Param("userId") UUID userId, @Param("fingerprint") String fingerprint);

    @Query("SELECT ds FROM DeviceSessionEntity ds WHERE ds.user.id = :userId AND ds.isActive = true AND ds.deletedAt IS NULL ORDER BY ds.lastActiveAt DESC")
    List<DeviceSessionEntity> findActiveSessionsByUser(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE DeviceSessionEntity ds SET ds.isActive = false, ds.revokedAt = :now, ds.updatedAt = :now WHERE ds.id = :sessionId")
    void revokeSession(@Param("sessionId") UUID sessionId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE DeviceSessionEntity ds SET ds.isActive = false, ds.revokedAt = :now, ds.updatedAt = :now WHERE ds.user.id = :userId AND ds.isActive = true")
    int revokeAllSessionsForUser(@Param("userId") UUID userId, @Param("now") Instant now);
}
