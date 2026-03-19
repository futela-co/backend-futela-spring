package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.request.auth.LoginRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;
import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.domain.exception.UnauthorizedException;
import com.futela.api.domain.model.auth.DeviceSession;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private DeviceSessionRepositoryPort deviceSessionRepository;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    private User activeUser;
    private DeviceSession deviceSession;
    private UUID userId;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        activeUser = new User(
                userId, "test@futela.com", "$2a$12$hashedpassword",
                "Jean", "Dupont", "+243999999999", null,
                UserRole.USER, UserStatus.ACTIVE,
                false, true, false,
                null, Instant.now(),
                companyId, "Futela",
                Instant.now(), Instant.now(), null
        );

        deviceSession = new DeviceSession(
                sessionId, userId, "Chrome", "fingerprint-123",
                null, null, null, true, false,
                Instant.now(), null, Instant.now(), Instant.now()
        );
    }

    @Test
    @DisplayName("Doit authentifier un utilisateur avec des identifiants valides")
    void shouldLoginSuccessfullyWithValidCredentials() {
        LoginRequest request = new LoginRequest("test@futela.com", "password123", "fingerprint-123", "Chrome");

        when(userRepository.findByEmailOrPhone("test@futela.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password123", activeUser.passwordHash())).thenReturn(true);
        when(deviceSessionRepository.findActiveByUserAndFingerprint(eq(userId), eq("fingerprint-123")))
                .thenReturn(Optional.of(deviceSession));
        when(jwtTokenProvider.generateAccessToken(eq(activeUser), eq(sessionId))).thenReturn("access-token-jwt");
        when(jwtTokenProvider.hashRefreshToken(any())).thenReturn("hashed-refresh-token");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(jwtTokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = loginService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access-token-jwt");
        assertThat(response.sessionId()).isEqualTo(sessionId.toString());
        assertThat(response.user()).isNotNull();
        assertThat(response.user().email()).isEqualTo("test@futela.com");
        assertThat(response.tokenType()).isEqualTo("Bearer");

        verify(userRepository).updateLastLogin(userId);
    }

    @Test
    @DisplayName("Doit lever UnauthorizedException avec un mauvais mot de passe")
    void shouldThrowUnauthorizedExceptionWithWrongPassword() {
        LoginRequest request = new LoginRequest("test@futela.com", "wrongpassword", null, null);

        when(userRepository.findByEmailOrPhone("test@futela.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrongpassword", activeUser.passwordHash())).thenReturn(false);

        assertThatThrownBy(() -> loginService.execute(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Identifiants invalides");

        verify(userRepository, never()).updateLastLogin(any());
    }

    @Test
    @DisplayName("Doit lever UnauthorizedException avec un email inexistant")
    void shouldThrowUnauthorizedExceptionWithNonExistentEmail() {
        LoginRequest request = new LoginRequest("inconnu@futela.com", "password123", null, null);

        when(userRepository.findByEmailOrPhone("inconnu@futela.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.execute(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Identifiants invalides");
    }

    @Test
    @DisplayName("Doit créer une nouvelle DeviceSession si aucune session existante")
    void shouldCreateDeviceSessionWhenNoneExists() {
        LoginRequest request = new LoginRequest("test@futela.com", "password123", "new-fingerprint", "Firefox");

        when(userRepository.findByEmailOrPhone("test@futela.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password123", activeUser.passwordHash())).thenReturn(true);
        when(deviceSessionRepository.findActiveByUserAndFingerprint(eq(userId), eq("new-fingerprint")))
                .thenReturn(Optional.empty());
        when(deviceSessionRepository.save(any(DeviceSession.class))).thenReturn(deviceSession);
        when(jwtTokenProvider.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtTokenProvider.hashRefreshToken(any())).thenReturn("hashed-token");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(jwtTokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        loginService.execute(request);

        verify(deviceSessionRepository).save(any(DeviceSession.class));
    }

    @Test
    @DisplayName("Doit générer un fingerprint aléatoire si non fourni")
    void shouldGenerateRandomFingerprintWhenNotProvided() {
        LoginRequest request = new LoginRequest("test@futela.com", "password123", null, "Chrome");

        when(userRepository.findByEmailOrPhone("test@futela.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password123", activeUser.passwordHash())).thenReturn(true);
        when(deviceSessionRepository.findActiveByUserAndFingerprint(eq(userId), any()))
                .thenReturn(Optional.empty());
        when(deviceSessionRepository.save(any(DeviceSession.class))).thenReturn(deviceSession);
        when(jwtTokenProvider.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtTokenProvider.hashRefreshToken(any())).thenReturn("hashed-token");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(jwtTokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        loginService.execute(request);

        verify(deviceSessionRepository).findActiveByUserAndFingerprint(eq(userId), argThat(fp -> fp != null && !fp.isEmpty()));
    }
}
