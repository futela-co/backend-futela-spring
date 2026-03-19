package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.request.messaging.RespondToContactRequest;
import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.domain.enums.ContactStatus;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.infrastructure.persistence.entity.messaging.ContactEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaContactRepository;
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
class RespondToContactServiceTest {

    @Mock
    private JpaContactRepository contactRepository;

    @InjectMocks
    private RespondToContactService service;

    private UUID contactId;
    private UUID adminId;
    private ContactEntity contact;

    @BeforeEach
    void setUp() {
        contactId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        contact = new ContactEntity();
        setEntityId(contact, contactId);
        setEntityTimestamps(contact);
        contact.setFirstName("Jean");
        contact.setLastName("Dupont");
        contact.setEmail("jean@test.com");
        contact.setSubject("Question");
        contact.setMessage("Mon message de question.");
        contact.setStatus(ContactStatus.NEW);
    }

    @Test
    @DisplayName("Doit répondre et définir le statut RESPONDED")
    void shouldRespondAndSetStatusResponded() {
        RespondToContactRequest request = new RespondToContactRequest(
                "Merci pour votre message, voici la réponse.", "RESPONDED"
        );

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(ContactEntity.class))).thenReturn(contact);

        ContactResponse response = service.execute(contactId, request, adminId);

        assertThat(response).isNotNull();
        assertThat(contact.getStatus()).isEqualTo(ContactStatus.RESPONDED);
        assertThat(contact.getResponse()).isEqualTo("Merci pour votre message, voici la réponse.");
        assertThat(contact.getRespondedBy()).isEqualTo(adminId);
        assertThat(contact.getRespondedAt()).isNotNull();
    }

    @Test
    @DisplayName("Doit définir respondedAt et respondedBy")
    void shouldSetRespondedAtAndRespondedBy() {
        RespondToContactRequest request = new RespondToContactRequest(
                "Réponse détaillée au formulaire de contact.", "RESPONDED"
        );

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any())).thenReturn(contact);

        service.execute(contactId, request, adminId);

        assertThat(contact.getRespondedAt()).isNotNull();
        assertThat(contact.getRespondedBy()).isEqualTo(adminId);
    }

    @Test
    @DisplayName("Doit rejeter quand le contact n'existe pas")
    void shouldRejectWhenContactNotFound() {
        UUID unknownId = UUID.randomUUID();
        RespondToContactRequest request = new RespondToContactRequest(
                "Réponse au formulaire de contact.", "RESPONDED"
        );

        when(contactRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(unknownId, request, adminId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Doit rejeter quand le statut est invalide")
    void shouldRejectWhenStatusInvalid() {
        RespondToContactRequest request = new RespondToContactRequest(
                "Réponse au formulaire de contact.", "INVALID_STATUS"
        );

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        assertThatThrownBy(() -> service.execute(contactId, request, adminId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Statut invalide");
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
