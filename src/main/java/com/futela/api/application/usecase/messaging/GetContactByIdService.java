package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.GetContactByIdUseCase;
import com.futela.api.infrastructure.persistence.mapper.messaging.ContactPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetContactByIdService implements GetContactByIdUseCase {

    private final JpaContactRepository contactRepository;

    @Override
    public ContactResponse execute(UUID contactId) {
        return contactRepository.findById(contactId)
                .filter(c -> c.getDeletedAt() == null)
                .map(ContactPersistenceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", contactId.toString()));
    }
}
