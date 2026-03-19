package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private GetCurrentUserService getCurrentUserService;

    @Test
    @DisplayName("Doit retourner les informations de l'utilisateur depuis le repository")
    void shouldReturnUserFromRepository() {
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        User user = new User(
                userId, "jean@futela.com", "$2a$12$hash",
                "Jean", "Dupont", "+243999999999", null,
                UserRole.USER, UserStatus.ACTIVE,
                true, true, false,
                Instant.now(), Instant.now(),
                companyId, "Futela",
                Instant.now(), Instant.now(), null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = getCurrentUserService.execute(userId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.email()).isEqualTo("jean@futela.com");
        assertThat(response.firstName()).isEqualTo("Jean");
        assertThat(response.lastName()).isEqualTo("Dupont");
        assertThat(response.fullName()).isEqualTo("Jean Dupont");
        assertThat(response.companyId()).isEqualTo(companyId);
    }

    @Test
    @DisplayName("Doit lever ResourceNotFoundException si l'utilisateur n'existe pas")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getCurrentUserService.execute(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Utilisateur");
    }
}
