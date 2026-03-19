package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.request.auth.RefreshTokenRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;
import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.domain.exception.UnauthorizedException;
import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.domain.model.auth.RefreshToken;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import com.futela.api.domain.port.out.auth.RefreshTokenRepositoryPort;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshAccessTokenServiceTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private DeviceSessionRepositoryPort deviceSessionRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private UUID tokenId;
    private UUID sessionId;
    private UUID userId;
    private User user;
    private DeviceSession activeSession;

    @BeforeEach
    void setUp() {
        tokenId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = new User(
                userId, "test@futela.com", "$2a$12$hash",
                "Jean", "Dupont", null, null,
                UserRole.USER, UserStatus.ACTIVE,
                false, true, false,
                null, Instant.now(),
                UUID.randomUUID(), "Futela",
                Instant.now(), Instant.now(), null
        );

        activeSession = new DeviceSession(
                sessionId, userId, "Chrome", "fp-123",
                null, null, null, true, false,
                Instant.now(), null, Instant.now(), Instant.now()
        );
    }

    @Test
    @DisplayName("Doit rafraîchir le token avec succès")
    void shouldRefreshTokenSuccessfully() {
        RefreshTokenRequest request = new RefreshTokenRequest("raw-refresh-token");

        RefreshToken validToken = new RefreshToken(
                tokenId, sessionId, "hashed-token",
                Instant.now().plusSeconds(3600),
                null, false,
                Instant.now(), Instant.now()
        );

        when(jwtTokenProvider.hashRefreshToken(any())).thenReturn("hashed-token").thenReturn("new-hashed-token");
        when(refreshTokenRepository.findByTokenHash("hashed-token")).thenReturn(Optional.of(validToken));
        when(deviceSessionRepository.findById(sessionId)).thenReturn(Optional.of(activeSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(eq(user), eq(sessionId))).thenReturn("new-access-token");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(jwtTokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = refreshTokenService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.user().email()).isEqualTo("test@futela.com");

        verify(refreshTokenRepository).markAsUsed(tokenId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Doit lever UnauthorizedException avec un token expiré")
    void shouldThrowUnauthorizedExceptionWithExpiredToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("expired-token");

        RefreshToken expiredToken = new RefreshToken(
                tokenId, sessionId, "hashed-expired",
                Instant.now().minusSeconds(3600), // expiré
                null, false,
                Instant.now().minusSeconds(7200), Instant.now().minusSeconds(7200)
        );

        when(jwtTokenProvider.hashRefreshToken("expired-token")).thenReturn("hashed-expired");
        when(refreshTokenRepository.findByTokenHash("hashed-expired")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> refreshTokenService.execute(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("expiré");
    }

    @Test
    @DisplayName("Doit lever UnauthorizedException avec un token révoqué")
    void shouldThrowUnauthorizedExceptionWithRevokedToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("revoked-token");

        RefreshToken revokedToken = new RefreshToken(
                tokenId, sessionId, "hashed-revoked",
                Instant.now().plusSeconds(3600),
                null, true, // revoked
                Instant.now(), Instant.now()
        );

        when(jwtTokenProvider.hashRefreshToken("revoked-token")).thenReturn("hashed-revoked");
        when(refreshTokenRepository.findByTokenHash("hashed-revoked")).thenReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> refreshTokenService.execute(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("révoqué");
    }

    @Test
    @DisplayName("Doit effectuer la rotation des tokens (ancien marqué comme utilisé, nouveau créé)")
    void shouldRotateTokens() {
        RefreshTokenRequest request = new RefreshTokenRequest("raw-refresh-token");

        RefreshToken validToken = new RefreshToken(
                tokenId, sessionId, "hashed-token",
                Instant.now().plusSeconds(3600),
                null, false,
                Instant.now(), Instant.now()
        );

        when(jwtTokenProvider.hashRefreshToken(any())).thenReturn("hashed-token").thenReturn("new-hashed-token");
        when(refreshTokenRepository.findByTokenHash("hashed-token")).thenReturn(Optional.of(validToken));
        when(deviceSessionRepository.findById(sessionId)).thenReturn(Optional.of(activeSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(any(), any())).thenReturn("new-access-token");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(jwtTokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = refreshTokenService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
        assertThat(response.refreshToken()).isNotEqualTo("raw-refresh-token");

        // L'ancien token doit être marqué comme utilisé
        verify(refreshTokenRepository).markAsUsed(tokenId);
        // Un nouveau refresh token doit être sauvegardé
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Doit détecter une replay attack et révoquer la session")
    void shouldDetectReplayAttackAndRevokeSession() {
        RefreshTokenRequest request = new RefreshTokenRequest("used-token");

        RefreshToken usedToken = new RefreshToken(
                tokenId, sessionId, "hashed-used",
                Instant.now().plusSeconds(3600),
                Instant.now().minusSeconds(60), // usedAt non null = déjà utilisé
                false,
                Instant.now(), Instant.now()
        );

        when(jwtTokenProvider.hashRefreshToken("used-token")).thenReturn("hashed-used");
        when(refreshTokenRepository.findByTokenHash("hashed-used")).thenReturn(Optional.of(usedToken));

        assertThatThrownBy(() -> refreshTokenService.execute(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("déjà été utilisé");

        verify(deviceSessionRepository).revokeSession(sessionId);
    }
}
