package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.UnreadCountResponse;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUnreadNotificationsCountServiceTest {

    @Mock
    private JpaNotificationRepository notificationRepository;

    @InjectMocks
    private GetUnreadNotificationsCountService service;

    @Test
    @DisplayName("Doit retourner le nombre correct de notifications non lues")
    void shouldReturnCorrectUnreadCount() {
        UUID userId = UUID.randomUUID();
        when(notificationRepository.countUnreadByUserId(userId)).thenReturn(3L);

        UnreadCountResponse response = service.execute(userId);

        assertThat(response.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Doit retourner 0 quand toutes les notifications sont lues")
    void shouldReturnZeroWhenAllRead() {
        UUID userId = UUID.randomUUID();
        when(notificationRepository.countUnreadByUserId(userId)).thenReturn(0L);

        UnreadCountResponse response = service.execute(userId);

        assertThat(response.count()).isZero();
    }
}
