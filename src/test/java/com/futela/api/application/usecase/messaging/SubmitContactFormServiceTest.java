package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.request.messaging.SubmitContactRequest;
import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.domain.enums.ContactStatus;
import com.futela.api.domain.event.ContactFormSubmittedEvent;
import com.futela.api.infrastructure.persistence.entity.messaging.ContactEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaContactRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitContactFormServiceTest {

    @Mock
    private JpaContactRepository contactRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SubmitContactFormService service;

    @Test
    @DisplayName("Doit soumettre le formulaire de contact avec succès")
    void shouldSubmitContactFormSuccessfully() {
        SubmitContactRequest request = new SubmitContactRequest(
                "Jean", "Dupont", "jean@test.com", "+243123456",
                "Question sur propriété", "Je souhaite plus d'informations sur cette propriété."
        );

        when(contactRepository.save(any(ContactEntity.class))).thenAnswer(invocation -> {
            ContactEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        ContactResponse response = service.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.firstName()).isEqualTo("Jean");
        assertThat(response.lastName()).isEqualTo("Dupont");
        assertThat(response.email()).isEqualTo("jean@test.com");
    }

    @Test
    @DisplayName("Doit créer le contact avec le statut NEW")
    void shouldCreateContactWithStatusNew() {
        SubmitContactRequest request = new SubmitContactRequest(
                "Marie", "Martin", "marie@test.com", null,
                "Demande", "Je voudrais savoir le prix de location."
        );

        ArgumentCaptor<ContactEntity> captor = ArgumentCaptor.forClass(ContactEntity.class);
        when(contactRepository.save(captor.capture())).thenAnswer(invocation -> {
            ContactEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        service.execute(request);

        assertThat(captor.getValue().getStatus()).isEqualTo(ContactStatus.NEW);
    }

    @Test
    @DisplayName("Doit émettre un ContactFormSubmittedEvent")
    void shouldEmitContactFormSubmittedEvent() {
        SubmitContactRequest request = new SubmitContactRequest(
                "Jean", "Dupont", "jean@test.com", null,
                "Question", "Mon message de test pour le contact."
        );

        when(contactRepository.save(any(ContactEntity.class))).thenAnswer(invocation -> {
            ContactEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        service.execute(request);

        ArgumentCaptor<ContactFormSubmittedEvent> eventCaptor = ArgumentCaptor.forClass(ContactFormSubmittedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        ContactFormSubmittedEvent event = eventCaptor.getValue();
        assertThat(event.name()).isEqualTo("Jean Dupont");
        assertThat(event.email()).isEqualTo("jean@test.com");
        assertThat(event.subject()).isEqualTo("Question");
    }

    @Test
    @DisplayName("Doit trimmer les champs du formulaire")
    void shouldTrimFormFields() {
        SubmitContactRequest request = new SubmitContactRequest(
                "  Jean  ", "  Dupont  ", "  jean@test.com  ", "  +243123  ",
                "  Sujet  ", "  Message de test de contact  "
        );

        ArgumentCaptor<ContactEntity> captor = ArgumentCaptor.forClass(ContactEntity.class);
        when(contactRepository.save(captor.capture())).thenAnswer(invocation -> {
            ContactEntity entity = invocation.getArgument(0);
            setEntityId(entity, UUID.randomUUID());
            setEntityTimestamps(entity);
            return entity;
        });

        service.execute(request);

        ContactEntity saved = captor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("Jean");
        assertThat(saved.getLastName()).isEqualTo("Dupont");
        assertThat(saved.getEmail()).isEqualTo("jean@test.com");
        assertThat(saved.getPhone()).isEqualTo("+243123");
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
