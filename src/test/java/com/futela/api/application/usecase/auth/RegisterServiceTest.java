package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.request.auth.RegisterRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;
import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.domain.exception.DuplicateResourceException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.model.core.Company;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import com.futela.api.domain.port.out.auth.RefreshTokenRepositoryPort;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import com.futela.api.domain.port.out.common.CompanyRepositoryPort;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private CompanyRepositoryPort companyRepository;

    @Mock
    private DeviceSessionRepositoryPort deviceSessionRepository;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterService registerService;

    private Company defaultCompany;
    private UUID companyId;
    private UUID userId;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();

        defaultCompany = new Company(
                companyId, "Futela", "futela",
                null, null, null, true,
                Instant.now(), Instant.now()
        );
    }

    @Test
    @DisplayName("Doit inscrire un nouvel utilisateur avec succès")
    void shouldRegisterNewUserSuccessfully() {
        RegisterRequest request = new RegisterRequest(
                "Jean", "Dupont", "jean@futela.com", "+243999999999",
                "password123", "fingerprint-123", "Chrome"
        );

        when(userRepository.emailExists("jean@futela.com")).thenReturn(false);
        when(userRepository.phoneExists("+243999999999")).thenReturn(false);
        when(companyRepository.findOrCreateDefault()).thenReturn(defaultCompany);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$hashedpassword");

        User savedUser = new User(
                userId, "jean@futela.com", "$2a$12$hashedpassword",
                "Jean", "Dupont", "+243999999999", null,
                UserRole.USER, UserStatus.ACTIVE,
                false, true, false,
                null, Instant.now(),
                companyId, "Futela",
                Instant.now(), Instant.now(), null
        );
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        DeviceSession savedSession = new DeviceSession(
                sessionId, userId, "Chrome", "fingerprint-123",
                null, null, null, true, false,
                Instant.now(), null, Instant.now(), Instant.now()
        );
        when(deviceSessionRepository.save(any(DeviceSession.class))).thenReturn(savedSession);
        when(jwtTokenProvider.generateAccessToken(eq(savedUser), eq(sessionId))).thenReturn("access-token");
        when(jwtTokenProvider.hashRefreshToken(any())).thenReturn("hashed-refresh");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(jwtTokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = registerService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.user().firstName()).isEqualTo("Jean");
        assertThat(response.user().lastName()).isEqualTo("Dupont");
        assertThat(response.user().email()).isEqualTo("jean@futela.com");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Doit lever DuplicateResourceException si l'email existe déjà")
    void shouldThrowDuplicateResourceExceptionWhenEmailExists() {
        RegisterRequest request = new RegisterRequest(
                "Jean", "Dupont", "existant@futela.com", null,
                "password123", null, null
        );

        when(userRepository.emailExists("existant@futela.com")).thenReturn(true);

        assertThatThrownBy(() -> registerService.execute(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email existe déjà");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit lever DuplicateResourceException si le téléphone existe déjà")
    void shouldThrowDuplicateResourceExceptionWhenPhoneExists() {
        RegisterRequest request = new RegisterRequest(
                "Jean", "Dupont", null, "+243999999999",
                "password123", null, null
        );

        when(userRepository.phoneExists("+243999999999")).thenReturn(true);

        assertThatThrownBy(() -> registerService.execute(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("numéro de téléphone existe déjà");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit hasher le mot de passe et ne pas le stocker en clair")
    void shouldHashPasswordAndNotStorePlainText() {
        RegisterRequest request = new RegisterRequest(
                "Jean", "Dupont", "jean@futela.com", null,
                "monMotDePasse", null, null
        );

        when(userRepository.emailExists(any())).thenReturn(false);
        when(companyRepository.findOrCreateDefault()).thenReturn(defaultCompany);
        when(passwordEncoder.encode("monMotDePasse")).thenReturn("$2a$12$encryptedHash");

        User savedUser = new User(
                userId, "jean@futela.com", "$2a$12$encryptedHash",
                "Jean", "Dupont", null, null,
                UserRole.USER, UserStatus.ACTIVE,
                false, true, false,
                null, Instant.now(),
                companyId, "Futela",
                Instant.now(), Instant.now(), null
        );
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        DeviceSession savedSession = new DeviceSession(
                sessionId, userId, null, UUID.randomUUID().toString(),
                null, null, null, true, false,
                Instant.now(), null, Instant.now(), Instant.now()
        );
        when(deviceSessionRepository.save(any())).thenReturn(savedSession);
        when(jwtTokenProvider.generateAccessToken(any(), any())).thenReturn("token");
        when(jwtTokenProvider.hashRefreshToken(any())).thenReturn("hash");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(jwtTokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        registerService.execute(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertThat(capturedUser.passwordHash()).isEqualTo("$2a$12$encryptedHash");
        assertThat(capturedUser.passwordHash()).isNotEqualTo("monMotDePasse");
        verify(passwordEncoder).encode("monMotDePasse");
    }

    @Test
    @DisplayName("Doit lever ValidationException si ni email ni téléphone ne sont fournis")
    void shouldThrowValidationExceptionWhenNoIdentifierProvided() {
        RegisterRequest request = new RegisterRequest(
                "Jean", "Dupont", null, null,
                "password123", null, null
        );

        assertThatThrownBy(() -> registerService.execute(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("email ou un numéro de téléphone est requis");
    }
}
