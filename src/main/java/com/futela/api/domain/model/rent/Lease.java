package com.futela.api.domain.model.rent;

import com.futela.api.domain.enums.LeaseStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Lease(
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
        UUID companyId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public boolean isActive() {
        return status == LeaseStatus.ACTIVE;
    }

    public boolean canBeRenewed() {
        return status == LeaseStatus.ACTIVE || status == LeaseStatus.EXPIRED;
    }
}
