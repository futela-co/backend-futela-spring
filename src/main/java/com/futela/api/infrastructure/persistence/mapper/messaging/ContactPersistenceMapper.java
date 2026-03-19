package com.futela.api.infrastructure.persistence.mapper.messaging;

import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.domain.model.messaging.Contact;
import com.futela.api.infrastructure.persistence.entity.messaging.ContactEntity;

public final class ContactPersistenceMapper {

    private ContactPersistenceMapper() {
    }

    public static Contact toDomain(ContactEntity entity) {
        return new Contact(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getSubject(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getResponse(),
                entity.getRespondedAt(),
                entity.getRespondedBy(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                null,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public static ContactResponse toResponse(ContactEntity entity) {
        return new ContactResponse(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getSubject(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getStatus().label(),
                entity.getStatus().color(),
                entity.getResponse(),
                entity.getRespondedAt(),
                entity.getRespondedBy(),
                entity.getCreatedAt()
        );
    }
}
