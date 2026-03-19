package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.domain.enums.ContactStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetContactSubmissionsUseCase {

    Page<ContactResponse> execute(ContactStatus status, Pageable pageable);
}
