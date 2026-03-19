package com.futela.api.domain.port.out.auth;

import com.futela.api.domain.model.auth.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken refreshToken);

    void markAsUsed(UUID id);

    void revokeAllTokensForSession(UUID sessionId);
}
