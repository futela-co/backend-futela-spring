package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveConversationServiceTest {

    @Mock
    private JpaConversationRepository conversationRepository;

    @InjectMocks
    private ArchiveConversationService service;

    private UUID conversationId;
    private UUID userId;
    private ConversationEntity conversation;

    @BeforeEach
    void setUp() {
        conversationId = UUID.randomUUID();
        userId = UUID.randomUUID();

        CompanyEntity company = new CompanyEntity();
        setEntityId(company, UUID.randomUUID());

        UserEntity user = new UserEntity();
        setEntityId(user, userId);
        user.setFirstName("Jean");
        user.setLastName("Dupont");
        user.setCompany(company);

        conversation = new ConversationEntity();
        setEntityId(conversation, conversationId);
        setEntityTimestamps(conversation);
        conversation.setSubject("Test");
        conversation.setArchived(false);
        conversation.setCompany(company);
        conversation.getParticipants().add(user);
    }

    @Test
    @DisplayName("Doit archiver la conversation (isArchived = true)")
    void shouldSetArchivedToTrue() {
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(conversationRepository.save(any())).thenReturn(conversation);

        ConversationResponse response = service.execute(conversationId, userId, true);

        assertThat(response).isNotNull();
        assertThat(conversation.isArchived()).isTrue();
        verify(conversationRepository).save(conversation);
    }

    @Test
    @DisplayName("Doit rejeter quand l'utilisateur ne participe pas à la conversation")
    void shouldRejectWhenUserNotParticipant() {
        UUID nonParticipantId = UUID.randomUUID();
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));

        assertThatThrownBy(() -> service.execute(conversationId, nonParticipantId, true))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("participez pas");
    }

    @Test
    @DisplayName("Doit rejeter quand la conversation n'existe pas")
    void shouldRejectWhenConversationNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(conversationRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(unknownId, userId, true))
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
