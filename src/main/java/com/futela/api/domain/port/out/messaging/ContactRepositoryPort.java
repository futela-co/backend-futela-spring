package com.futela.api.domain.port.out.messaging;

import com.futela.api.domain.enums.ContactStatus;
import com.futela.api.domain.model.messaging.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ContactRepositoryPort {

    Contact save(Contact contact);

    Optional<Contact> findById(UUID id);

    Page<Contact> findAll(Pageable pageable);

    Page<Contact> findByStatus(ContactStatus status, Pageable pageable);

    void softDelete(UUID id);
}
