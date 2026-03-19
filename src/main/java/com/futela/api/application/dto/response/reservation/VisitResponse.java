package com.futela.api.application.dto.response.reservation;

import com.futela.api.domain.enums.VisitStatus;

import java.time.Instant;
import java.util.UUID;

public record VisitResponse(
        UUID id,
        UUID propertyId,
        String propertyTitle,
        UUID userId,
        String userFullName,
        VisitStatus status,
        String statusLabel,
        String statusColor,
        Instant scheduledAt,
        String notes,
        String cancelReason,
        Instant confirmedAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static String labelFor(VisitStatus status) {
        return switch (status) {
            case SCHEDULED -> "Programmée";
            case CONFIRMED -> "Confirmée";
            case CANCELLED -> "Annulée";
            case COMPLETED -> "Effectuée";
        };
    }

    public static String colorFor(VisitStatus status) {
        return switch (status) {
            case SCHEDULED -> "yellow";
            case CONFIRMED -> "green";
            case CANCELLED -> "red";
            case COMPLETED -> "blue";
        };
    }
}
