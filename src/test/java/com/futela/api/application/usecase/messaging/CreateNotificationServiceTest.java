package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;
import com.futela.api.domain.enums.NotificationChannel;
import com.futela.api.domain.enums.NotificationStatus;
import com.futela.api.domain.enums.NotificationType;
import com.futela.api.domain.event.NotificationCreatedEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.NotificationEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateNotificationServiceTest {

    @Mock
    private JpaNotificationRepository notificationRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CreateNotificationService service;

    private UUID userId;
    private UserEntity user;
    private CompanyEntity company;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        company = new CompanyEntity();
        setEntityId(company, UUID.randomUUID());

        user = new UserEntity();
        setEntityId(user, userId);
        user.setFirstName("Jean");
        user.setLastName("Dupont");
        user.setCompany(company);
    }

    @Test
    @DisplayName("Doit créer une notification avec succès")
    void shouldCreateNotificationSuccessfully() {
        UUID relatedEntityId = UUID.randomUUID();
        Map<String, Object> data = Map.of("key", "value");

        when(entityManager.find(UserEntity.class, userId)).thenReturn(user);
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(invocation -> {
            NotificationEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        NotificationResponse response = service.execute(
                userId, NotificationType.MESSAGE, "Titre", "Corps",
                NotificationChannel.IN_APP, data, relatedEntityId, "message"
        );

        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(NotificationType.MESSAGE);
        assertThat(response.status()).isEqualTo(NotificationStatus.UNREAD);
        assertThat(response.title()).isEqualTo("Titre");
    }

    @Test
    @DisplayName("Doit émettre un NotificationCreatedEvent")
    void shouldEmitNotificationCreatedEvent() {
        when(entityManager.find(UserEntity.class, userId)).thenReturn(user);
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(invocation -> {
            NotificationEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        service.execute(userId, NotificationType.PAYMENT, "Paiement reçu", "Votre paiement a été reçu",
                NotificationChannel.IN_APP, null, UUID.randomUUID(), "payment");

        ArgumentCaptor<NotificationCreatedEvent> captor = ArgumentCaptor.forClass(NotificationCreatedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        NotificationCreatedEvent event = captor.getValue();
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.type()).isEqualTo(NotificationType.PAYMENT);
        assertThat(event.title()).isEqualTo("Paiement reçu");
    }

    @Test
    @DisplayName("Doit rejeter quand l'utilisateur n'existe pas")
    void shouldRejectWhenUserNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        when(entityManager.find(UserEntity.class, unknownUserId)).thenReturn(null);

        assertThatThrownBy(() -> service.execute(
                unknownUserId, NotificationType.SYSTEM, "Test", "Test",
                NotificationChannel.IN_APP, null, null, null
        )).isInstanceOf(ResourceNotFoundException.class);
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
