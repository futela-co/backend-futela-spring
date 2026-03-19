package com.futela.api.domain.model.reservation;

import com.futela.api.domain.enums.VisitStatus;

import java.time.Instant;
import java.util.UUID;

public record Visit(
        UUID id,
        UUID propertyId,
        UUID userId,
        UUID companyId,
        VisitStatus status,
        Instant scheduledAt,
        String notes,
        String cancelReason,
        Instant confirmedAt,
        Instant completedAt,
        String paymentTransactionId,
        boolean isPaid,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {}
