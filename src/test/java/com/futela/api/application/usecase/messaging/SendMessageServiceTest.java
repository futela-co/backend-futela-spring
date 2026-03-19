package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.request.messaging.SendMessageRequest;
import com.futela.api.application.dto.response.messaging.MessageResponse;
import com.futela.api.domain.enums.MessageType;
import com.futela.api.domain.event.MessageSentEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.MessageEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendMessageServiceTest {

    @Mock
    private JpaConversationRepository conversationRepository;

    @Mock
    private JpaMessageRepository messageRepository;

    @Mock
    private JpaNotificationRepository notificationRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SendMessageService service;

    private UUID conversationId;
    private UUID senderId;
    private UUID otherUserId;
    private ConversationEntity conversation;
    private UserEntity sender;
    private UserEntity otherUser;
    private CompanyEntity company;

    @BeforeEach
    void setUp() {
        conversationId = UUID.randomUUID();
        senderId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        company = new CompanyEntity();
        setEntityId(company, UUID.randomUUID());

        sender = new UserEntity();
        setEntityId(sender, senderId);
        sender.setFirstName("Jean");
        sender.setLastName("Dupont");
        sender.setCompany(company);

        otherUser = new UserEntity();
        setEntityId(otherUser, otherUserId);
        otherUser.setFirstName("Marie");
        otherUser.setLastName("Martin");
        otherUser.setCompany(company);

        conversation = new ConversationEntity();
        setEntityId(conversation, conversationId);
        conversation.setSubject("Test");
        conversation.setCompany(company);
        conversation.getParticipants().add(sender);
        conversation.getParticipants().add(otherUser);
    }

    @Test
    @DisplayName("Doit envoyer un message TEXT avec succès")
    void shouldSendTextMessageSuccessfully() {
        SendMessageRequest request = new SendMessageRequest("Bonjour !", MessageType.TEXT);

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(entityManager.find(UserEntity.class, senderId)).thenReturn(sender);
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(invocation -> {
            MessageEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });
        when(conversationRepository.save(any())).thenReturn(conversation);

        MessageResponse response = service.execute(conversationId, request, senderId);

        assertThat(response).isNotNull();
        assertThat(response.content()).isEqualTo("Bonjour !");
        assertThat(response.type()).isEqualTo(MessageType.TEXT);
    }

    @Test
    @DisplayName("Doit envoyer un message IMAGE avec succès")
    void shouldSendImageMessageSuccessfully() {
        SendMessageRequest request = new SendMessageRequest("https://img.url/photo.jpg", MessageType.IMAGE);

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(entityManager.find(UserEntity.class, senderId)).thenReturn(sender);
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(invocation -> {
            MessageEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });
        when(conversationRepository.save(any())).thenReturn(conversation);

        MessageResponse response = service.execute(conversationId, request, senderId);

        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(MessageType.IMAGE);
    }

    @Test
    @DisplayName("Doit mettre à jour lastMessageAt de la conversation")
    void shouldUpdateConversationLastMessageAt() {
        SendMessageRequest request = new SendMessageRequest("Test", MessageType.TEXT);

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(entityManager.find(UserEntity.class, senderId)).thenReturn(sender);
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(invocation -> {
            MessageEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        ArgumentCaptor<ConversationEntity> convCaptor = ArgumentCaptor.forClass(ConversationEntity.class);
        when(conversationRepository.save(convCaptor.capture())).thenReturn(conversation);

        service.execute(conversationId, request, senderId);

        assertThat(convCaptor.getValue().getLastMessageAt()).isNotNull();
    }

    @Test
    @DisplayName("Doit émettre un MessageSentEvent")
    void shouldEmitMessageSentEvent() {
        SendMessageRequest request = new SendMessageRequest("Bonjour", MessageType.TEXT);

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(entityManager.find(UserEntity.class, senderId)).thenReturn(sender);
        when(messageRepository.save(any(MessageEntity.class))).thenAnswer(invocation -> {
            MessageEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });
        when(conversationRepository.save(any())).thenReturn(conversation);

        service.execute(conversationId, request, senderId);

        ArgumentCaptor<MessageSentEvent> eventCaptor = ArgumentCaptor.forClass(MessageSentEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        MessageSentEvent event = eventCaptor.getValue();
        assertThat(event.conversationId()).isEqualTo(conversationId);
        assertThat(event.senderId()).isEqualTo(senderId);
        assertThat(event.senderName()).isEqualTo("Jean Dupont");
    }

    @Test
    @DisplayName("Doit rejeter quand l'utilisateur ne participe pas à la conversation")
    void shouldRejectWhenUserNotParticipant() {
        UUID nonParticipantId = UUID.randomUUID();
        UserEntity nonParticipant = new UserEntity();
        setEntityId(nonParticipant, nonParticipantId);
        nonParticipant.setFirstName("Inconnu");
        nonParticipant.setLastName("Test");

        SendMessageRequest request = new SendMessageRequest("Test", MessageType.TEXT);

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(entityManager.find(UserEntity.class, nonParticipantId)).thenReturn(nonParticipant);

        assertThatThrownBy(() -> service.execute(conversationId, request, nonParticipantId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("participe pas");
    }

    @Test
    @DisplayName("Doit rejeter quand la conversation n'existe pas")
    void shouldRejectWhenConversationNotFound() {
        SendMessageRequest request = new SendMessageRequest("Test", MessageType.TEXT);
        UUID unknownId = UUID.randomUUID();

        when(conversationRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(unknownId, request, senderId))
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
