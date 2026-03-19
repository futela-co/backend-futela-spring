package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.MessageResponse;
import com.futela.api.domain.enums.MessageType;
import com.futela.api.domain.event.MessageReadEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.MessageEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
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
class MarkMessageAsReadServiceTest {

    @Mock
    private JpaMessageRepository messageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MarkMessageAsReadService service;

    private UUID messageId;
    private UUID senderId;
    private UUID recipientId;
    private MessageEntity message;
    private ConversationEntity conversation;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        senderId = UUID.randomUUID();
        recipientId = UUID.randomUUID();

        CompanyEntity company = new CompanyEntity();
        setEntityId(company, UUID.randomUUID());

        UserEntity sender = new UserEntity();
        setEntityId(sender, senderId);
        sender.setFirstName("Jean");
        sender.setLastName("Dupont");
        sender.setCompany(company);

        UserEntity recipient = new UserEntity();
        setEntityId(recipient, recipientId);
        recipient.setFirstName("Marie");
        recipient.setLastName("Martin");
        recipient.setCompany(company);

        conversation = new ConversationEntity();
        setEntityId(conversation, UUID.randomUUID());
        conversation.setSubject("Test");
        conversation.setCompany(company);
        conversation.getParticipants().add(sender);
        conversation.getParticipants().add(recipient);

        message = new MessageEntity();
        setEntityId(message, messageId);
        setEntityTimestamps(message);
        message.setSender(sender);
        message.setConversation(conversation);
        message.setContent("Bonjour");
        message.setType(MessageType.TEXT);
        message.setRead(false);
        message.setCompany(company);
    }

    @Test
    @DisplayName("Doit marquer le message comme lu avec readAt défini")
    void shouldMarkMessageAsReadWithReadAtSet() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageRepository.save(any(MessageEntity.class))).thenReturn(message);

        MessageResponse response = service.execute(messageId, recipientId);

        assertThat(response).isNotNull();
        assertThat(message.isRead()).isTrue();
        assertThat(message.getReadAt()).isNotNull();
        verify(messageRepository).save(message);
    }

    @Test
    @DisplayName("Doit rejeter quand l'expéditeur essaie de marquer son propre message comme lu")
    void shouldRejectWhenSenderTriesToMarkOwnMessage() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        assertThatThrownBy(() -> service.execute(messageId, senderId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("propre message");
    }

    @Test
    @DisplayName("Doit être idempotent quand le message est déjà lu")
    void shouldBeIdempotentWhenAlreadyRead() {
        message.setRead(true);
        message.setReadAt(Instant.now().minusSeconds(60));

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        MessageResponse response = service.execute(messageId, recipientId);

        assertThat(response).isNotNull();
        verify(messageRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(MessageReadEvent.class));
    }

    @Test
    @DisplayName("Doit émettre un MessageReadEvent")
    void shouldEmitMessageReadEvent() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageRepository.save(any())).thenReturn(message);

        service.execute(messageId, recipientId);

        ArgumentCaptor<MessageReadEvent> captor = ArgumentCaptor.forClass(MessageReadEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        assertThat(captor.getValue().messageId()).isEqualTo(messageId);
        assertThat(captor.getValue().readByUserId()).isEqualTo(recipientId);
    }

    @Test
    @DisplayName("Doit rejeter quand le message n'existe pas")
    void shouldRejectWhenMessageNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(messageRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(unknownId, recipientId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Doit rejeter quand l'utilisateur ne participe pas à la conversation")
    void shouldRejectWhenUserNotParticipant() {
        UUID nonParticipantId = UUID.randomUUID();
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        assertThatThrownBy(() -> service.execute(messageId, nonParticipantId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("participe pas");
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
