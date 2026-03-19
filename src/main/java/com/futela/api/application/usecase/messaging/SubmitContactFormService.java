package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.request.messaging.SubmitContactRequest;
import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.domain.enums.ContactStatus;
import com.futela.api.domain.event.ContactFormSubmittedEvent;
import com.futela.api.domain.port.in.messaging.SubmitContactFormUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.ContactEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.ContactPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubmitContactFormService implements SubmitContactFormUseCase {

    private final JpaContactRepository contactRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ContactResponse execute(SubmitContactRequest request) {
        ContactEntity entity = new ContactEntity();
        entity.setFirstName(request.firstName().trim());
        entity.setLastName(request.lastName().trim());
        entity.setEmail(request.email().trim());
        entity.setPhone(request.phone() != null ? request.phone().trim() : null);
        entity.setSubject(request.subject().trim());
        entity.setMessage(request.message().trim());
        entity.setStatus(ContactStatus.NEW);

        ContactEntity saved = contactRepository.save(entity);

        log.info("Formulaire de contact soumis : {} | Email: {} | Sujet: {}",
                saved.getId(), saved.getEmail(), saved.getSubject());

        eventPublisher.publishEvent(new ContactFormSubmittedEvent(
                saved.getId(),
                saved.getFirstName() + " " + saved.getLastName(),
                saved.getEmail(),
                saved.getSubject()
        ));

        return ContactPersistenceMapper.toResponse(saved);
    }
}
