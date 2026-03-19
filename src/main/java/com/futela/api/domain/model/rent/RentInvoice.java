package com.futela.api.domain.model.rent;

import com.futela.api.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RentInvoice(
        UUID id,
        UUID leaseId,
        String invoiceNumber,
        BigDecimal amount,
        BigDecimal paidAmount,
        PaymentStatus status,
        LocalDate dueDate,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal lateFee,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt
) {
    public boolean canBePaid() {
        return status == PaymentStatus.PENDING
                || status == PaymentStatus.OVERDUE
                || status == PaymentStatus.PARTIAL;
    }

    public boolean isOverdue() {
        return status == PaymentStatus.OVERDUE
                || (status != PaymentStatus.PAID && dueDate.isBefore(LocalDate.now()));
    }

    public BigDecimal remainingAmount() {
        return amount.subtract(paidAmount);
    }
}
