package com.futela.api.application.dto.response.rent;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.model.rent.RentInvoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RentInvoiceResponse(
        UUID id,
        UUID leaseId,
        String invoiceNumber,
        BigDecimal amount,
        BigDecimal paidAmount,
        BigDecimal remainingAmount,
        PaymentStatus status,
        LocalDate dueDate,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal lateFee,
        Instant createdAt
) {
    public static RentInvoiceResponse from(RentInvoice invoice) {
        return new RentInvoiceResponse(
                invoice.id(),
                invoice.leaseId(),
                invoice.invoiceNumber(),
                invoice.amount(),
                invoice.paidAmount(),
                invoice.remainingAmount(),
                invoice.status(),
                invoice.dueDate(),
                invoice.periodStart(),
                invoice.periodEnd(),
                invoice.lateFee(),
                invoice.createdAt()
        );
    }
}
