package com.futela.api.application.dto.response.messaging;

import com.futela.api.domain.enums.ContactStatus;

import java.time.Instant;
import java.util.UUID;

public record ContactResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String subject,
        String message,
        ContactStatus status,
        String statusLabel,
        String statusColor,
        String response,
        Instant respondedAt,
        UUID respondedBy,
        Instant createdAt
) {
}
