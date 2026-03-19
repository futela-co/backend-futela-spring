package com.futela.api.domain.model.messaging;

import com.futela.api.domain.enums.ContactStatus;

import java.time.Instant;
import java.util.UUID;

public record Contact(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String subject,
        String message,
        ContactStatus status,
        String response,
        Instant respondedAt,
        UUID respondedBy,
        String ipAddress,
        String userAgent,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
}
