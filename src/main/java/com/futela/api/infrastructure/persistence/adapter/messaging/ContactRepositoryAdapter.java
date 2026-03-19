package com.futela.api.infrastructure.persistence.adapter.messaging;

import com.futela.api.domain.enums.ContactStatus;
import com.futela.api.domain.model.messaging.Contact;
import com.futela.api.domain.port.out.messaging.ContactRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.messaging.ContactEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.ContactPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactRepositoryAdapter implements ContactRepositoryPort {

    private final JpaContactRepository jpaRepository;

    @Override
    public Contact save(Contact contact) {
        ContactEntity entity = contact.id() != null
                ? jpaRepository.findById(contact.id()).orElse(new ContactEntity())
                : new ContactEntity();
        entity.setFirstName(contact.firstName());
        entity.setLastName(contact.lastName());
        entity.setEmail(contact.email());
        entity.setPhone(contact.phone());
        entity.setSubject(contact.subject());
        entity.setMessage(contact.message());
        entity.setStatus(contact.status());
        entity.setResponse(contact.response());
        entity.setRespondedAt(contact.respondedAt());
        entity.setRespondedBy(contact.respondedBy());
        entity.setIpAddress(contact.ipAddress());
        entity.setUserAgent(contact.userAgent());
        return ContactPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Contact> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(ContactPersistenceMapper::toDomain);
    }

    @Override
    public Page<Contact> findAll(Pageable pageable) {
        return jpaRepository.findAllActive(pageable)
                .map(ContactPersistenceMapper::toDomain);
    }

    @Override
    public Page<Contact> findByStatus(ContactStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status, pageable)
                .map(ContactPersistenceMapper::toDomain);
    }

    @Override
    public void softDelete(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.softDelete();
            jpaRepository.save(entity);
        });
    }
}
