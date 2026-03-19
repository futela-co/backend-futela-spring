package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.request.auth.RefreshTokenRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;
import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.domain.exception.UnauthorizedException;
import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.domain.model.auth.RefreshToken;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.port.in.auth.RefreshAccessTokenUseCase;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import com.futela.api.domain.port.out.auth.RefreshTokenRepositoryPort;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService implements RefreshAccessTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final DeviceSessionRepositoryPort deviceSessionRepository;
    private final UserRepositoryPort userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse execute(RefreshTokenRequest request) {
        // 1. Hash le refresh token
        String tokenHash = jwtTokenProvider.hashRefreshToken(request.refreshToken());

        // 2. Trouver le refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Jeton de rafraîchissement invalide"));

        // 3. Vérifications de sécurité
        if (refreshToken.isRevoked()) {
            throw new UnauthorizedException("Le jeton de rafraîchissement a été révoqué");
        }
        if (refreshToken.isExpired()) {
            throw new UnauthorizedException("Le jeton de rafraîchissement a expiré");
        }

        // 4. Détection de replay attack
        if (refreshToken.isUsed()) {
            deviceSessionRepository.revokeSession(refreshToken.deviceSessionId());
            throw new UnauthorizedException("Le jeton de rafraîchissement a déjà été utilisé. Session révoquée par sécurité.");
        }

        // 5. Vérifier la session
        DeviceSession deviceSession = deviceSessionRepository.findById(refreshToken.deviceSessionId())
                .orElseThrow(() -> new UnauthorizedException("Session introuvable"));

        if (!deviceSession.isActive()) {
            throw new UnauthorizedException("La session a été révoquée");
        }

        // 6. Récupérer l'utilisateur
        User user = userRepository.findById(deviceSession.userId())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable"));

        // 7. Marquer l'ancien token comme utilisé
        refreshTokenRepository.markAsUsed(refreshToken.id());

        // 8. Générer les nouveaux tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user, deviceSession.id());
        String newRawRefreshToken = UUID.randomUUID().toString();
        String newTokenHash = jwtTokenProvider.hashRefreshToken(newRawRefreshToken);
        Instant expiresAt = Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationMs());

        RefreshToken newRefreshToken = new RefreshToken(
                null, deviceSession.id(), newTokenHash, expiresAt,
                null, false, Instant.now(), Instant.now()
        );
        refreshTokenRepository.save(newRefreshToken);

        return new AuthResponse(
                newAccessToken,
                newRawRefreshToken,
                deviceSession.id().toString(),
                (int) (jwtTokenProvider.getAccessTokenExpirationMs() / 1000),
                (int) (jwtTokenProvider.getRefreshTokenExpirationMs() / 1000),
                UserResponse.fromDomain(user)
        );
    }
}
