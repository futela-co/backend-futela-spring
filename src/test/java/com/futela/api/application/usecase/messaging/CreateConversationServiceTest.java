package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.request.messaging.CreateConversationRequest;
import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.domain.event.ConversationCreatedEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateConversationServiceTest {

    @Mock
    private JpaConversationRepository conversationRepository;

    @Mock
    private JpaMessageRepository messageRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CreateConversationService service;

    private UUID currentUserId;
    private UUID participant2Id;
    private UserEntity participant1;
    private UserEntity participant2;
    private CompanyEntity company;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        participant2Id = UUID.randomUUID();

        company = new CompanyEntity();
        setEntityId(company, UUID.randomUUID());

        participant1 = new UserEntity();
        setEntityId(participant1, currentUserId);
        participant1.setFirstName("Jean");
        participant1.setLastName("Dupont");
        participant1.setCompany(company);

        participant2 = new UserEntity();
        setEntityId(participant2, participant2Id);
        participant2.setFirstName("Marie");
        participant2.setLastName("Martin");
        participant2.setCompany(company);
    }

    @Test
    @DisplayName("Doit créer une nouvelle conversation entre 2 utilisateurs")
    void shouldCreateNewConversationBetweenTwoUsers() {
        CreateConversationRequest request = new CreateConversationRequest(participant2Id, "Sujet test", null);

        when(conversationRepository.findByParticipants(currentUserId, participant2Id))
                .thenReturn(Optional.empty());
        when(entityManager.getReference(UserEntity.class, currentUserId)).thenReturn(participant1);
        when(entityManager.find(UserEntity.class, participant2Id)).thenReturn(participant2);
        when(conversationRepository.save(any(ConversationEntity.class))).thenAnswer(invocation -> {
            ConversationEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        ConversationResponse response = service.execute(request, currentUserId);

        assertThat(response).isNotNull();
        assertThat(response.subject()).isEqualTo("Sujet test");
        verify(conversationRepository).save(any(ConversationEntity.class));
    }

    @Test
    @DisplayName("Doit réutiliser une conversation existante entre les mêmes participants")
    void shouldReuseExistingConversation() {
        CreateConversationRequest request = new CreateConversationRequest(participant2Id, "Sujet test", null);

        ConversationEntity existingConversation = new ConversationEntity();
        setEntityId(existingConversation, UUID.randomUUID());
        setEntityTimestamps(existingConversation);
        existingConversation.setSubject("Sujet existant");
        existingConversation.setCompany(company);
        existingConversation.getParticipants().add(participant1);
        existingConversation.getParticipants().add(participant2);

        when(conversationRepository.findByParticipants(currentUserId, participant2Id))
                .thenReturn(Optional.of(existingConversation));

        ConversationResponse response = service.execute(request, currentUserId);

        assertThat(response).isNotNull();
        assertThat(response.subject()).isEqualTo("Sujet existant");
        verify(conversationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit créer une conversation avec propertyId")
    void shouldCreateConversationWithPropertyId() {
        UUID propertyId = UUID.randomUUID();
        CreateConversationRequest request = new CreateConversationRequest(participant2Id, "Question propriété", propertyId);

        when(conversationRepository.findByParticipantsAndProperty(currentUserId, participant2Id, propertyId))
                .thenReturn(Optional.empty());
        when(entityManager.getReference(UserEntity.class, currentUserId)).thenReturn(participant1);
        when(entityManager.find(UserEntity.class, participant2Id)).thenReturn(participant2);

        PropertyEntity propertyRef = mock(PropertyEntity.class);
        when(propertyRef.getId()).thenReturn(propertyId);
        when(entityManager.getReference(PropertyEntity.class, propertyId)).thenReturn(propertyRef);

        ArgumentCaptor<ConversationEntity> captor = ArgumentCaptor.forClass(ConversationEntity.class);
        when(conversationRepository.save(captor.capture())).thenAnswer(invocation -> {
            ConversationEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        service.execute(request, currentUserId);

        assertThat(captor.getValue().getProperty()).isNotNull();
        assertThat(captor.getValue().getProperty().getId()).isEqualTo(propertyId);
    }

    @Test
    @DisplayName("Doit émettre un ConversationCreatedEvent")
    void shouldEmitConversationCreatedEvent() {
        CreateConversationRequest request = new CreateConversationRequest(participant2Id, "Sujet", null);

        when(conversationRepository.findByParticipants(currentUserId, participant2Id))
                .thenReturn(Optional.empty());
        when(entityManager.getReference(UserEntity.class, currentUserId)).thenReturn(participant1);
        when(entityManager.find(UserEntity.class, participant2Id)).thenReturn(participant2);
        when(conversationRepository.save(any(ConversationEntity.class))).thenAnswer(invocation -> {
            ConversationEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        service.execute(request, currentUserId);

        ArgumentCaptor<ConversationCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ConversationCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        ConversationCreatedEvent event = eventCaptor.getValue();
        assertThat(event.subject()).isEqualTo("Sujet");
        assertThat(event.participantIds()).containsExactlyInAnyOrder(currentUserId, participant2Id);
    }

    @Test
    @DisplayName("Doit rejeter quand le participant 2 n'existe pas")
    void shouldRejectWhenParticipant2NotFound() {
        CreateConversationRequest request = new CreateConversationRequest(participant2Id, "Sujet", null);

        when(conversationRepository.findByParticipants(currentUserId, participant2Id))
                .thenReturn(Optional.empty());
        when(entityManager.getReference(UserEntity.class, currentUserId)).thenReturn(participant1);
        when(entityManager.find(UserEntity.class, participant2Id)).thenReturn(null);

        assertThatThrownBy(() -> service.execute(request, currentUserId))
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
