package com.futela.api.application.usecase.messaging;

import com.futela.api.domain.enums.NotificationStatus;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarkAllNotificationsAsReadServiceTest {

    @Mock
    private JpaNotificationRepository notificationRepository;

    @InjectMocks
    private MarkAllNotificationsAsReadService service;

    @Test
    @DisplayName("Doit marquer toutes les notifications non lues comme lues pour l'utilisateur")
    void shouldMarkAllUnreadNotificationsAsRead() {
        UUID userId = UUID.randomUUID();

        service.execute(userId);

        verify(notificationRepository).markAllAsReadByUserId(
                eq(userId),
                any(Instant.class),
                eq(NotificationStatus.READ)
        );
    }

    @Test
    @DisplayName("Doit appeler le repository même quand il n'y a pas de notifications non lues")
    void shouldCallRepositoryEvenWhenNoUnreadNotifications() {
        UUID userId = UUID.randomUUID();

        service.execute(userId);

        verify(notificationRepository).markAllAsReadByUserId(
                eq(userId),
                any(Instant.class),
                eq(NotificationStatus.READ)
        );
    }
}
