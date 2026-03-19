package com.futela.api.application.usecase.messaging;

import com.futela.api.domain.enums.MessageType;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.MessageEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteMessageServiceTest {

    @Mock
    private JpaMessageRepository messageRepository;

    @InjectMocks
    private DeleteMessageService service;

    private UUID messageId;
    private UUID senderId;
    private MessageEntity message;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        senderId = UUID.randomUUID();

        CompanyEntity company = new CompanyEntity();
        setEntityId(company, UUID.randomUUID());

        UserEntity sender = new UserEntity();
        setEntityId(sender, senderId);
        sender.setFirstName("Jean");
        sender.setLastName("Dupont");
        sender.setCompany(company);

        ConversationEntity conversation = new ConversationEntity();
        setEntityId(conversation, UUID.randomUUID());
        conversation.setCompany(company);

        message = new MessageEntity();
        setEntityId(message, messageId);
        setEntityTimestamps(message);
        message.setSender(sender);
        message.setConversation(conversation);
        message.setContent("Test");
        message.setType(MessageType.TEXT);
        message.setCompany(company);
    }

    @Test
    @DisplayName("Doit supprimer le message avec succès (soft delete)")
    void shouldSoftDeleteMessageSuccessfully() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageRepository.save(any(MessageEntity.class))).thenReturn(message);

        service.execute(messageId, senderId);

        assertThat(message.getDeletedAt()).isNotNull();
        verify(messageRepository).save(message);
    }

    @Test
    @DisplayName("Doit rejeter quand un non-expéditeur essaie de supprimer")
    void shouldRejectWhenNonSenderTriesToDelete() {
        UUID otherUserId = UUID.randomUUID();
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        assertThatThrownBy(() -> service.execute(messageId, otherUserId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("propres messages");
    }

    @Test
    @DisplayName("Doit rejeter quand le message n'existe pas")
    void shouldRejectWhenMessageNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(messageRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(unknownId, senderId))
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
