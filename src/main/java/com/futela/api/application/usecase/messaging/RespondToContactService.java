package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.request.messaging.RespondToContactRequest;
import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.domain.enums.ContactStatus;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.exception.ValidationException;
import com.futela.api.domain.port.in.messaging.RespondToContactUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.ContactEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.ContactPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RespondToContactService implements RespondToContactUseCase {

    private final JpaContactRepository contactRepository;

    @Override
    public ContactResponse execute(UUID contactId, RespondToContactRequest request, UUID respondedByUserId) {
        ContactEntity contact = contactRepository.findById(contactId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", contactId.toString()));

        ContactStatus newStatus;
        try {
            newStatus = ContactStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide : " + request.status());
        }

        contact.setResponse(request.response().trim());
        contact.setRespondedAt(Instant.now());
        contact.setRespondedBy(respondedByUserId);
        contact.setStatus(newStatus);

        contactRepository.save(contact);

        log.info("Réponse au contact {} avec statut {}", contactId, newStatus);

        return ContactPersistenceMapper.toResponse(contact);
    }
}
