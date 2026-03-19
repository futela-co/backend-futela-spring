package com.futela.api.application.dto.response.reservation;

import com.futela.api.domain.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UUID propertyId,
        String propertyTitle,
        UUID userId,
        String userFullName,
        UUID hostId,
        ReservationStatus status,
        String statusLabel,
        String statusColor,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice,
        String currency,
        int guestCount,
        String notes,
        String cancelReason,
        Instant confirmedAt,
        Instant cancelledAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static String labelFor(ReservationStatus status) {
        return switch (status) {
            case PENDING -> "En attente";
            case CONFIRMED -> "Confirmée";
            case CANCELLED -> "Annulée";
            case COMPLETED -> "Terminée";
        };
    }

    public static String colorFor(ReservationStatus status) {
        return switch (status) {
            case PENDING -> "yellow";
            case CONFIRMED -> "green";
            case CANCELLED -> "red";
            case COMPLETED -> "blue";
        };
    }
}
