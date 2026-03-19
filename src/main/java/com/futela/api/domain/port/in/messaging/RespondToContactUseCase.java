package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.request.messaging.RespondToContactRequest;
import com.futela.api.application.dto.response.messaging.ContactResponse;

import java.util.UUID;

public interface RespondToContactUseCase {

    ContactResponse execute(UUID contactId, RespondToContactRequest request, UUID respondedByUserId);
}
