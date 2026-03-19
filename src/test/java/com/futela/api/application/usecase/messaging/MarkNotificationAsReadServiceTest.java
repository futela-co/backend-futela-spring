package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;
import com.futela.api.domain.enums.NotificationChannel;
import com.futela.api.domain.enums.NotificationStatus;
import com.futela.api.domain.enums.NotificationType;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.NotificationEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkNotificationAsReadServiceTest {

    @Mock
    private JpaNotificationRepository notificationRepository;

    @InjectMocks
    private MarkNotificationAsReadService service;

    private UUID notificationId;
    private UUID userId;
    private NotificationEntity notification;

    @BeforeEach
    void setUp() {
        notificationId = UUID.randomUUID();
        userId = UUID.randomUUID();

        CompanyEntity company = new CompanyEntity();
        setEntityId(company, UUID.randomUUID());

        UserEntity user = new UserEntity();
        setEntityId(user, userId);
        user.setFirstName("Jean");
        user.setLastName("Dupont");
        user.setCompany(company);

        notification = new NotificationEntity();
        setEntityId(notification, notificationId);
        setEntityTimestamps(notification);
        notification.setUser(user);
        notification.setType(NotificationType.MESSAGE);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setTitle("Nouveau message");
        notification.setBody("Vous avez un nouveau message");
        notification.setChannel(NotificationChannel.IN_APP);
        notification.setData(Map.of());
        notification.setRead(false);
        notification.setCompany(company);
    }

    @Test
    @DisplayName("Doit marquer la notification comme lue avec readAt défini")
    void shouldMarkAsReadWithReadAtSet() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(notification);

        NotificationResponse response = service.execute(notificationId, userId);

        assertThat(response).isNotNull();
        assertThat(notification.isRead()).isTrue();
        assertThat(notification.getReadAt()).isNotNull();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.READ);
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Doit rejeter quand la notification n'appartient pas à l'utilisateur")
    void shouldRejectWhenNotificationNotOwnedByUser() {
        UUID otherUserId = UUID.randomUUID();
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> service.execute(notificationId, otherUserId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("appartient pas");
    }

    @Test
    @DisplayName("Doit être idempotent quand la notification est déjà lue")
    void shouldBeIdempotentWhenAlreadyRead() {
        notification.setRead(true);
        notification.setReadAt(Instant.now().minusSeconds(60));
        notification.setStatus(NotificationStatus.READ);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        NotificationResponse response = service.execute(notificationId, userId);

        assertThat(response).isNotNull();
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit rejeter quand la notification n'existe pas")
    void shouldRejectWhenNotificationNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(notificationRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(unknownId, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private void setEntityId(Object entity, UUID id) {
        try {
            var clazz = entity.getClass();
            while (clazz != null) {
                try {
                    var field = clazz.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, id);
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setEntityTimestamps(Object entity) {
        try {
            var clazz = entity.getClass();
            while (clazz != null) {
                try {
                    var createdAt = clazz.getDeclaredField("createdAt");
                    createdAt.setAccessible(true);
                    createdAt.set(entity, Instant.now());
                    var updatedAt = clazz.getDeclaredField("updatedAt");
                    updatedAt.setAccessible(true);
                    updatedAt.set(entity, Instant.now());
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
