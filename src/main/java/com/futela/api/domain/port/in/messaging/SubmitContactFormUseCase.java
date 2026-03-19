package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.request.messaging.SubmitContactRequest;
import com.futela.api.application.dto.response.messaging.ContactResponse;

public interface SubmitContactFormUseCase {

    ContactResponse execute(SubmitContactRequest request);
}
