package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.domain.enums.ContactStatus;
import com.futela.api.domain.port.in.messaging.GetContactSubmissionsUseCase;
import com.futela.api.infrastructure.persistence.mapper.messaging.ContactPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetContactSubmissionsService implements GetContactSubmissionsUseCase {

    private final JpaContactRepository contactRepository;

    @Override
    public Page<ContactResponse> execute(ContactStatus status, Pageable pageable) {
        if (status != null) {
            return contactRepository.findByStatus(status, pageable)
                    .map(ContactPersistenceMapper::toResponse);
        }
        return contactRepository.findAllActive(pageable)
                .map(ContactPersistenceMapper::toResponse);
    }
}
