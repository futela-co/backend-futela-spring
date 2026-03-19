package com.futela.api.domain.model.reservation;

import com.futela.api.domain.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Reservation(
        UUID id,
        UUID propertyId,
        UUID userId,
        UUID hostId,
        UUID companyId,
        ReservationStatus status,
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
        String paymentTransactionId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {}
