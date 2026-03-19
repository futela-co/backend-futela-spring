package com.futela.api.application.dto.response.rent;

import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.model.rent.Lease;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record LeaseResponse(
        UUID id,
        UUID propertyId,
        String propertyTitle,
        UUID tenantId,
        String tenantName,
        UUID landlordId,
        String landlordName,
        LeaseStatus status,
        BigDecimal monthlyRent,
        String currency,
        BigDecimal depositAmount,
        LocalDate startDate,
        LocalDate endDate,
        int paymentDayOfMonth,
        String notes,
        Instant terminatedAt,
        String terminationReason,
        Instant createdAt
) {
    public static LeaseResponse from(Lease lease) {
        return new LeaseResponse(
                lease.id(),
                lease.propertyId(),
                lease.propertyTitle(),
                lease.tenantId(),
                lease.tenantName(),
                lease.landlordId(),
                lease.landlordName(),
                lease.status(),
                lease.monthlyRent(),
                lease.currency(),
                lease.depositAmount(),
                lease.startDate(),
                lease.endDate(),
                lease.paymentDayOfMonth(),
                lease.notes(),
                lease.terminatedAt(),
                lease.terminationReason(),
                lease.createdAt()
        );
    }
}
