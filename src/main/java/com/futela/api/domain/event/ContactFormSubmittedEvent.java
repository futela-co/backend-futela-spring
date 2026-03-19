package com.futela.api.domain.event;

import java.util.UUID;

public record ContactFormSubmittedEvent(
        UUID contactId,
        String name,
        String email,
        String subject
) {
}
