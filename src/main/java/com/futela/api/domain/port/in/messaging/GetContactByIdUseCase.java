package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.ContactResponse;

import java.util.UUID;

public interface GetContactByIdUseCase {

    ContactResponse execute(UUID contactId);
}
