package com.futela.api.application.usecase.auth;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import com.futela.api.domain.port.out.auth.RefreshTokenRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private DeviceSessionRepositoryPort deviceSessionRepository;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    @DisplayName("Doit déconnecter avec succès en révoquant les tokens et la session")
    void shouldLogoutSuccessfully() {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        DeviceSession session = new DeviceSession(
                sessionId, userId, "Chrome", "fp-123",
                null, null, null, true, false,
                Instant.now(), null, Instant.now(), Instant.now()
        );

        when(deviceSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        logoutService.execute(sessionId);

        verify(refreshTokenRepository).revokeAllTokensForSession(sessionId);
        verify(deviceSessionRepository).revokeSession(sessionId);
    }

    @Test
    @DisplayName("Doit lever ResourceNotFoundException si la session n'existe pas")
    void shouldThrowResourceNotFoundExceptionWhenSessionNotFound() {
        UUID sessionId = UUID.randomUUID();

        when(deviceSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logoutService.execute(sessionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Session");

        verify(refreshTokenRepository, never()).revokeAllTokensForSession(any());
        verify(deviceSessionRepository, never()).revokeSession(any());
    }
}
