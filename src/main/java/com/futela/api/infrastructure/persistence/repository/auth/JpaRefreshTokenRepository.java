package com.futela.api.infrastructure.persistence.repository.auth;

import com.futela.api.infrastructure.persistence.entity.auth.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.usedAt = :now, rt.updatedAt = :now WHERE rt.id = :id")
    void markAsUsed(@Param("id") UUID id, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.isRevoked = true, rt.updatedAt = :now WHERE rt.deviceSession.id = :sessionId AND rt.isRevoked = false")
    void revokeAllTokensForSession(@Param("sessionId") UUID sessionId, @Param("now") Instant now);
}
