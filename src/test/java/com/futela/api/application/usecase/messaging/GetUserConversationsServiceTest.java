package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserConversationsServiceTest {

    @Mock
    private JpaConversationRepository conversationRepository;

    @InjectMocks
    private GetUserConversationsService service;

    private UUID userId;
    private CompanyEntity company;
    private UserEntity user;

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
    @DisplayName("Doit retourner les conversations triées par lastMessageAt DESC")
    void shouldReturnConversationsSortedByLastMessageAtDesc() {
        ConversationEntity conv1 = createConversation("Conv 1", Instant.now().minusSeconds(60), false);
        ConversationEntity conv2 = createConversation("Conv 2", Instant.now(), false);

        when(conversationRepository.findByParticipantId(userId))
                .thenReturn(List.of(conv2, conv1));

        List<ConversationResponse> result = service.execute(userId, false);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).subject()).isEqualTo("Conv 2");
        assertThat(result.get(1).subject()).isEqualTo("Conv 1");
    }

    @Test
    @DisplayName("Doit exclure les conversations archivées quand includeArchived est false")
    void shouldExcludeArchivedConversations() {
        ConversationEntity active = createConversation("Active", Instant.now(), false);
        ConversationEntity archived = createConversation("Archivée", Instant.now().minusSeconds(120), true);

        when(conversationRepository.findByParticipantId(userId))
                .thenReturn(List.of(active, archived));

        List<ConversationResponse> result = service.execute(userId, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).subject()).isEqualTo("Active");
    }

    @Test
    @DisplayName("Doit inclure les conversations archivées quand includeArchived est true")
    void shouldIncludeArchivedConversations() {
        ConversationEntity active = createConversation("Active", Instant.now(), false);
        ConversationEntity archived = createConversation("Archivée", Instant.now().minusSeconds(120), true);

        when(conversationRepository.findByParticipantId(userId))
                .thenReturn(List.of(active, archived));

        List<ConversationResponse> result = service.execute(userId, true);

        assertThat(result).hasSize(2);
    }

    private ConversationEntity createConversation(String subject, Instant lastMessageAt, boolean archived) {
        ConversationEntity conv = new ConversationEntity();
        setEntityId(conv, UUID.randomUUID());
        setEntityTimestamps(conv);
        conv.setSubject(subject);
        conv.setLastMessageAt(lastMessageAt);
        conv.setArchived(archived);
        conv.setCompany(company);
        conv.getParticipants().add(user);
        return conv;
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
