package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.UnreadCountResponse;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
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
class GetUnreadMessagesCountServiceTest {

    @Mock
    private JpaMessageRepository messageRepository;

    @InjectMocks
    private GetUnreadMessagesCountService service;

    @Test
    @DisplayName("Doit retourner le nombre correct de messages non lus")
    void shouldReturnCorrectUnreadCount() {
        UUID userId = UUID.randomUUID();
        when(messageRepository.countUnreadByUserId(userId)).thenReturn(5L);

        UnreadCountResponse response = service.execute(userId);

        assertThat(response.count()).isEqualTo(5);
    }

    @Test
    @DisplayName("Doit retourner 0 quand tous les messages sont lus")
    void shouldReturnZeroWhenAllRead() {
        UUID userId = UUID.randomUUID();
        when(messageRepository.countUnreadByUserId(userId)).thenReturn(0L);

        UnreadCountResponse response = service.execute(userId);

        assertThat(response.count()).isZero();
    }
}
