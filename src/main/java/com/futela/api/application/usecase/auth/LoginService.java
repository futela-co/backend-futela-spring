package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.request.auth.LoginRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;
import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.domain.exception.UnauthorizedException;
import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.domain.model.auth.RefreshToken;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.port.in.auth.LoginUseCase;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import com.futela.api.domain.port.out.auth.RefreshTokenRepositoryPort;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final DeviceSessionRepositoryPort deviceSessionRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse execute(LoginRequest request) {
        // 1. Trouver l'utilisateur par email OU téléphone
        User user = userRepository.findByEmailOrPhone(request.username())
                .orElseThrow(() -> new UnauthorizedException("Identifiants invalides"));

        // 2. Vérifier le mot de passe
        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new UnauthorizedException("Identifiants invalides");
        }

        // 3. Créer ou récupérer la session d'appareil
        String fingerprint = request.deviceFingerprint() != null ? request.deviceFingerprint() : UUID.randomUUID().toString();
        DeviceSession deviceSession = findOrCreateSession(user.id(), fingerprint, request.deviceName());

        // 4. Mettre à jour lastLoginAt
        userRepository.updateLastLogin(user.id());

        // 5. Générer les tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user, deviceSession.id());
        String refreshTokenString = generateAndSaveRefreshToken(deviceSession.id());

        return new AuthResponse(
                accessToken,
                refreshTokenString,
                deviceSession.id().toString(),
                (int) (jwtTokenProvider.getAccessTokenExpirationMs() / 1000),
                (int) (jwtTokenProvider.getRefreshTokenExpirationMs() / 1000),
                UserResponse.fromDomain(user)
        );
    }

    private DeviceSession findOrCreateSession(UUID userId, String fingerprint, String deviceName) {
        return deviceSessionRepository.findActiveByUserAndFingerprint(userId, fingerprint)
                .orElseGet(() -> {
                    DeviceSession session = new DeviceSession(
                            null, userId, deviceName, fingerprint,
                            null, null, null, true, false,
                            Instant.now(), null, Instant.now(), Instant.now()
                    );
                    return deviceSessionRepository.save(session);
                });
    }

    private String generateAndSaveRefreshToken(UUID sessionId) {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = jwtTokenProvider.hashRefreshToken(rawToken);
        Instant expiresAt = Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationMs());

        RefreshToken refreshToken = new RefreshToken(
                null, sessionId, tokenHash, expiresAt,
                null, false, Instant.now(), Instant.now()
        );
        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }
}
